package es.shosha.shosha.negocio;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.shosha.shosha.MyApplication;
import es.shosha.shosha.dominio.Lista;
import es.shosha.shosha.persistencia.ChecksumPers;
import es.shosha.shosha.persistencia.ItemPers;
import es.shosha.shosha.persistencia.ListaPers;
import es.shosha.shosha.persistencia.ParticipaPers;
import es.shosha.shosha.persistencia.UsuarioPers;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

/**
 * Esta clase se encarga de comprobar los checksums de la base de datos (remota y local)
 * Para ello, comprueba los checksums locales y los compara con la base de datos remota.
 * Si alguno de ellos cambia, modifica la tabla local afectada, para mantenerla actualizada.
 * Esta clase es invocada en cualquier operación de modificación de la base de datos (Insert, Update y Delete),
 * y por una tarea programada.
 *
 * @author Jesús Iráizoz
 * @version 0.2
 */
public class NegocioChecksum {

    private static final String USUARIO = "usuario";
    private static final String LISTA = "lista";
    private static final String ITEM = "item";
    private static final String PARTICIPA = "participa";
    private static final String CODIGO_QR = "codigoQR";

    private NegocioChecksum() {
    }

    private static Map<String, Double> actualizaDatos(Map<String, Double> mapaRemoto) {

        AdaptadorBD abd = new AdaptadorBD(MyApplication.getAppContext());
        abd.open();
        Map<String, Double> mapaLocal = abd.obtenerChecksum();
        abd.close();

        if (mapaLocal == null || (mapaLocal.size() <= 0 && mapaRemoto.size() <= 0))
            return mapaRemoto;

        //TODO: CASCA AQUÍ <---------------------------------------------------------------

        for (String s : mapaRemoto.keySet()) {
            if (mapaLocal.containsValue(mapaRemoto.get(s)))
                mapaRemoto.remove(s);
        }

        return mapaRemoto;
    }

    /**
     * Método estático que se encarga de comprobar si los checksums de las bases de datos (local y remota) son los mismos.
     * Si no lo son, inserta los nuevos checksums en la base de datos local. De lo contrario, no hace nada.
     *
     * @param params    Si se necesitan, nombre de las tablas a obtener el checksum. Si es vacío obtiene todos
     * @return Booleano si hay que actualizar los datos.
     */
    public static Map<String, Double> setChecksum(String... params) {
        return setChecksum(false, params);
    }


    /**
     * Método estático que se encarga de comprobar si los checksums de las bases de datos (local y remota) son los mismos.
     * Si no lo son, inserta los nuevos checksums en la base de datos local. De lo contrario, no hace nada.
     *
     * @param firstLoad Para indicar si es la primera carga de los datos, si no lo es, poner a false
     * @param params    Si se necesitan, nombre de las tablas a obtener el checksum. Si es vacío obtiene todos
     * @return Booleano si hay que actualizar los datos.
     */
    public static Map<String, Double> setChecksum(Boolean firstLoad, String... params) {
        //Obtiene de la BD remota los Checksums
        ChecksumPers cp = new ChecksumPers();
        if (params.length > 0)
            cp.execute(params[0]);
        else
            cp.execute();

        Map<String, Double> mapaRemoto = null;

        try {
            // Crea un mapa con las tablas solicitadas
            mapaRemoto = cp.get();
            // Compara el mapa con los checksums locales, y devuelve el mapa actualizado con los que haya que actualizar
            mapaRemoto = NegocioChecksum.actualizaDatos(mapaRemoto);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (mapaRemoto.size() > 0 && !firstLoad)
            secuenciaInsercion(mapaRemoto);

        return mapaRemoto;
    }

    public static void secuenciaInsercion(Map<String, Double> mapa) {
        Map<String, Double> mapaTree = new TreeMap<>();
        //Transformo a mapaTree para asegurar el orden
        mapaTree.putAll(mapa);

        AdaptadorBD abd = new AdaptadorBD(MyApplication.getAppContext());
        abd.open();
        // Insertamos los checksums actualizados
        abd.insertarChecksum(mapaTree);

        //Insertamos las tablas que haya que actualizar
        final int N;

        if (mapaTree.containsKey(USUARIO) && mapaTree.containsKey(LISTA)) {
            N = 2;
        } else if (mapaTree.containsKey(USUARIO) || mapaTree.containsKey(LISTA)) {
            N = 1;
        } else {
            N = -1;
        }

        if (N > 0) {
            final CountDownLatch count = new CountDownLatch(N);
            ExecutorService pool = Executors.newFixedThreadPool(N);


            if (mapaTree.containsKey(USUARIO)) {
                UsuarioPers up = new UsuarioPers(MyApplication.getAppContext(), count);
                up.executeOnExecutor(pool, MyApplication.getUser().getId());
            }
            if (mapaTree.containsKey(LISTA)) {

                ListaPers lp = new ListaPers(MyApplication.getAppContext());
                lp.executeOnExecutor(pool, String.valueOf(MyApplication.getUser().getId()));

            /*ListaPers lp = new ListaPers(MyApplication.getAppContext(), null);
            lp.execute(String.valueOf(MyApplication.getUser().getId()));
            try {
                List<Lista> lLista = lp.get();
                for (Lista l : lLista)
                    abd.insertarLista(l.getId(), l.getNombre(), l.getPropietario(), l.isEstado() ? "1" : "0");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }*/
            }

            try {
                count.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (mapaTree.containsKey(ITEM)) {
            List<Lista> lListas = abd.obtenerListas(MyApplication.getUser());

            String[] idListas = new String[lListas.size()];
            for (int i = 0; i < lListas.size(); i++) {
                idListas[i] = String.valueOf(lListas.get(i).getId());
            }
            ItemPers ip = new ItemPers(MyApplication.getAppContext());
            ip.execute(idListas);
        }
        if (mapaTree.containsKey(PARTICIPA)) {
            List<Lista> lListas = abd.obtenerListas(MyApplication.getUser());
            ParticipaPers pp = new ParticipaPers(MyApplication.getAppContext(),null);
            pp.execute(ParticipaPers.MULTIPLES_LISTAS);
        }
        // En teoría, al insertar las listas se insertan sus correspondientes códigos QR
        /*if (mapaTree.containsKey(CODIGO_QR)) {
        }*/


        abd.close();

    }
}
