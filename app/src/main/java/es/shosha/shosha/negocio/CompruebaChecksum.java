package es.shosha.shosha.negocio;

import java.util.Map;

import es.shosha.shosha.MyApplication;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

/**
 * Created by Jesús Iráizoz on 04/04/2017.
 */

public class CompruebaChecksum {
    private CompruebaChecksum() {
    }

    public static boolean actualizaDatos(Map<String, Double> mapaRemoto) {

        AdaptadorBD abd = new AdaptadorBD(MyApplication.getAppContext());
        abd.open();
        Map<String, Double> mapaLocal = abd.obtenerChecksum();
        abd.close();

        if (mapaLocal == null || (mapaLocal.size() <= 0 && mapaRemoto.size() <= 0))
            return true;

        for (Double d : mapaRemoto.values()) {
            if (!mapaLocal.containsValue(d))
                return true;
        }

        return false;
    }
}
