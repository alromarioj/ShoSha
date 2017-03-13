package es.shosha.shosha.negocio;

import android.content.Context;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.shosha.shosha.dominio.Lista;
import es.shosha.shosha.dominio.Usuario;
import es.shosha.shosha.persistencia.ItemPers;
import es.shosha.shosha.persistencia.ListaPers;
import es.shosha.shosha.persistencia.UsuarioPers;
import es.shosha.shosha.persistencia.VersionPers;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

/**
 * Created by Jesús Iráizoz on 09/03/2017.
 */

public class CargaDatos implements Runnable {
    private Context contexto;
    private String idUsr;

    public CargaDatos(String idUsuario, Context c) {
        this.contexto = c;
        this.idUsr = idUsuario;
    }

    @Override
    public void run() {
        //Comprobamos primero si se han realizado cambios en la BD remota
        AdaptadorBD abd = new AdaptadorBD(this.contexto);
        abd.open();
        long bdLocal = abd.getUltimaModificacion(this.idUsr);
        VersionPers vp = new VersionPers();
        vp.execute(this.idUsr);
        Long lVp = null;
        long bdRemota = 0L;
        try {
            lVp = vp.get();
            bdRemota = lVp.longValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //Si no coindicen las BD, se realiza la inserción
        if (bdLocal != bdRemota) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println(">>>>>>>>>>>>>> Base de datos distinta >>>>>>>>>>>");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");

            try {
                final int N = 2;
                final CountDownLatch count = new CountDownLatch(N);
                ExecutorService pool = Executors.newFixedThreadPool(N);


                UsuarioPers up = new UsuarioPers(this.contexto, count);
                up.executeOnExecutor(pool, this.idUsr);

                ListaPers lp = new ListaPers(this.contexto, count);
                lp.executeOnExecutor(pool, this.idUsr);

                System.out.println(">>>>>>>>>>>>>> antes del await >>>>>>>>>>>");

                count.await();

                System.out.println(">>>>>>>>>>>>>> despues del await >>>>>>>>>>>");


           /*     Usuario u = abd.obtenerUsuario(this.idUsr);

                System.out.println(u.toString());*/


                ItemPers ip = new ItemPers(this.contexto);


                List<Lista> lListas = abd.obtenerListas(this.idUsr);

                String[] idListas = new String[lListas.size()];
                for (int i = 0; i < lListas.size(); i++) {
                    System.out.println(">>>>>>>>>>>>>>>>>        " + lListas.get(i).getId() + "               >>>>>>>>>>>>>>>>>");
                    idListas[i] = lListas.get(i).getId();
                }
                System.out.println(idListas);
                ip.execute(idListas);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
        abd.close();

    }


}