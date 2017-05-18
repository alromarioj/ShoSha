package es.shosha.shosha.negocio;

import android.content.Context;
import android.widget.TextView;

import es.shosha.shosha.MyApplication;
import es.shosha.shosha.R;
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

    public CargaDatos() {
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
    }
}
