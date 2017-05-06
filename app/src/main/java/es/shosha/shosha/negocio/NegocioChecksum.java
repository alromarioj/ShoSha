package es.shosha.shosha.negocio;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import es.shosha.shosha.MyApplication;
import es.shosha.shosha.persistencia.ChecksumPers;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

/**
 * Created by Jesús Iráizoz on 04/04/2017.
 */

public class NegocioChecksum {
    private NegocioChecksum() {
    }

    private static boolean actualizaDatos(Map<String, Double> mapaRemoto) {

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

    /**
     * Método estático que se encarga de comprobar si los checksums de las bases de datos (local y remota) son los mismos.
     * Si no lo son, inserta los nuevos checksums en la base de datos local. De lo contrario, no hace nada.
     *
     * @param params Si se necesita, nombre de las tablas a obtener el checksum. Si es vacío obtiene todos
     * @return Booleano si hay que actualizar los datos.
     */
    public static boolean setChecksum(String... params) {
        ChecksumPers cp = new ChecksumPers();
        if (params.length > 0)
            cp.execute(params[0]);
        else
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
        if (actualizar) {
            AdaptadorBD abd = new AdaptadorBD(MyApplication.getAppContext());
            abd.open();
            abd.insertarChecksum(mapaRemoto);
            abd.close();
        }
        return actualizar;
    }
}
