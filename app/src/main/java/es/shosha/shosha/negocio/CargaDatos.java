package es.shosha.shosha.negocio;

import android.content.Context;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.shosha.shosha.MyApplication;
import es.shosha.shosha.dominio.Lista;
import es.shosha.shosha.persistencia.ChecksumPers;
import es.shosha.shosha.dominio.Usuario;
import es.shosha.shosha.persistencia.ItemPers;
import es.shosha.shosha.persistencia.ListaPers;
import es.shosha.shosha.persistencia.UsuarioPers;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

/**
 * Created by Jesús Iráizoz on 09/03/2017.
 */

public class CargaDatos implements Runnable {
    private Context contexto;
    private int idUsr;

    public CargaDatos(int idUsuario, Context c) {
        this.contexto = c;
        this.idUsr = idUsuario;
    }

    @Override
    public void run() {
        //Comprobamos primero si se han realizado cambios en la BD remota
        //Para ello, comprobamos checksums
        AdaptadorBD abd = new AdaptadorBD(this.contexto);
        abd.open();

        ChecksumPers cp = new ChecksumPers();
        cp.execute();

        Map<String, Double> mapaRemoto = null;

        boolean actualizar = true;
        try {
            mapaRemoto = cp.get();

            actualizar = CompruebaChecksum.actualizaDatos(mapaRemoto);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //Si no coindicen las BD, se realiza la inserción
        if (actualizar) {
            //abd.insertarUltimaModificacion(new Date().getTime());
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println(">>>>>>>>>>>>>> Base de datos distinta >>>>>>>>>>>");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");

            abd.insertarChecksum(mapaRemoto);

            try {
                final int N = 2;
                final CountDownLatch count = new CountDownLatch(N);
                ExecutorService pool = Executors.newFixedThreadPool(N);


                UsuarioPers up = new UsuarioPers(this.contexto, count);
                up.executeOnExecutor(pool, this.idUsr);

                ListaPers lp = new ListaPers(this.contexto, count);
                lp.executeOnExecutor(pool, String.valueOf(this.idUsr));

                count.await();
                try {
                    MyApplication.setUser(up.get());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                ItemPers ip = new ItemPers(this.contexto);


                List<Lista> lListas = abd.obtenerListas(this.idUsr);

                String[] idListas = new String[lListas.size()];
                for (int i = 0; i < lListas.size(); i++) {
                    idListas[i] = String.valueOf(lListas.get(i).getId());
                }
                System.out.println(idListas);
                ip.execute(idListas);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        abd.close();

    }
}
