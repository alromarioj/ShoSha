package es.shosha.shosha.negocio;

import android.content.Context;

import es.shosha.shosha.persistencia.ItemFB;
import es.shosha.shosha.persistencia.ListaFB;
import es.shosha.shosha.persistencia.ParticipaFB;
import es.shosha.shosha.persistencia.UsuarioFB;

/**
 * Created by Jesús Iráizoz on 09/03/2017.
 */

public class CargaDatos implements Runnable {
    private static final int DELAY = 3000;
    private Context contexto;
    private int idUsr;

    public CargaDatos(int idUsuario, Context c) {
        this.contexto = c;
        this.idUsr = idUsuario;
    }

    @Override
    public void run() {
        synchronized (this) {
            try {
                new UsuarioFB();
                this.wait(DELAY);
                new ListaFB();
                this.wait(DELAY);
                new ItemFB();
                new ParticipaFB();
                this.wait(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //   UsuarioFB.insertaUsuarioFB(new Usuario(4,"Yisus","yisus@craist.org"));

        //Comprobamos primero si se han realizado cambios en la BD remota
        //Para ello, comprobamos checksums
       /* boolean actualizar = false;

        //TODO: Fallo de bucle infinito. buscarlo.

        Map<String, Double> mapaInsercion = NegocioChecksum.setChecksum(true);

        if (mapaInsercion != null && mapaInsercion.size() > 0)
            actualizar = true;

        *//*
        ChecksumPers cp = new ChecksumPers();
        cp.execute();

        Map<String, Double> mapaRemoto = null;

        boolean actualizar = true;
        try {
            mapaRemoto = cp.get();

            actualizar = NegocioChecksum.actualizaDatos(mapaRemoto);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        *//*

        //Si no coindicen las BD, se realiza la inserción
        if (actualizar) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println(">>>>>>>>>>>>>> Base de datos distinta >>>>>>>>>>>");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");

            AdaptadorBD abd = new AdaptadorBD(this.contexto);
            abd.open();
            //    abd.insertarChecksum(mapaRemoto);

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
            abd.close();
        }


        // Iniciamos un timer de los checksums
        new ChecksumEjecutorTimer();*/


    }
}
