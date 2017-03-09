package es.shosha.shosha.negocio;

import android.content.Context;

import java.util.List;

import es.shosha.shosha.dominio.Lista;
import es.shosha.shosha.persistencia.ItemPers;
import es.shosha.shosha.persistencia.ListaPers;
import es.shosha.shosha.persistencia.UsuarioPers;
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
        UsuarioPers up = new UsuarioPers(this.contexto);
        up.execute(this.idUsr);
        ListaPers lp = new ListaPers(this.contexto);
        lp.execute(this.idUsr);

        ItemPers ip = new ItemPers(this.contexto);

        AdaptadorBD abd = new AdaptadorBD(this.contexto);
        abd.open();
        List<Lista> lListas = abd.obtenerListas(this.idUsr);
        abd.close();
        String[] idListas = new String[lListas.size()];
        for (int i = 0; i < lListas.size(); i++) {
            System.out.println(">>>>>>>>>>>>>>>>>        " + lListas.get(i).getId() + "               >>>>>>>>>>>>>>>>>");
            idListas[i] = lListas.get(i).getId();
        }
        System.out.println(idListas);
        ip.execute(idListas);


        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

    }
}
