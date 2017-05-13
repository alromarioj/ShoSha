package es.shosha.shosha.persistencia;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.shosha.shosha.LectorQR;
import es.shosha.shosha.dominio.Usuario;
import es.shosha.shosha.negocio.NegocioChecksum;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

/**
 * Created by Jesús Iráizoz on 13/03/2017.
 */
public class ParticipaPers extends AsyncTask<String, Void, String> {
    private final static String URL_GET = "http://shosha.jiraizoz.es/getParticipaciones.php?";
    private final static String URL_ADD = "http://shosha.jiraizoz.es/addParticipante.php?";
    private final static String URL_ADD_QR = "http://shosha.jiraizoz.es/addParticipanteQR.php?";
    private final static String LISTA = "lista=";
    private final static String USUARIO = "usuario=";
    private final static String CLAVE = "clave=";
    private LectorQR lqr;
    private int lista,usuario;

    public static final String MULTIPLES_LISTAS = "multiple";
    public static final String INSERT = "insert";
    private static final String UTF_8 = "UTF-8";

    private Context contexto;
    private final CountDownLatch count;
    private String respuesta,accion="";

    public ParticipaPers(Context contexto, CountDownLatch count) {
        this.contexto = contexto;
        this.count = count;
    }
    public ParticipaPers(Context contexto, CountDownLatch count, LectorQR lectorQR) {
        this.contexto = contexto;
        this.count = count;
        this.lqr=lectorQR;
    }

    @Override
    protected String doInBackground(String... params) {
        String data = "",respuesta="";
        Usuario usu = null;
        if (params.length == 1) {
            try {
                data = URLEncoder.encode(params[0].toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                java.net.URL urlObj = new URL(URL_GET + LISTA + data);

                HttpURLConnection lu = (HttpURLConnection) urlObj.openConnection();

                BufferedReader rd = new BufferedReader(new InputStreamReader(lu.getInputStream()));
                String line = "", res = "";
                while ((line = rd.readLine()) != null) {
                    res += line;
                }

                rd.close();
                jsonParser(res);

                if (count != null)
                    count.countDown();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (params.length == 4 && params[0].equals("insert")) {
            accion="insert";
            insertMode(params[1], params[2], params[3]);
        }
        else {
            try {
                lanzadorExcepcion();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return respuesta;
    }

    /**
     * Añade un participante a una lista
     *
     * @param params 0:idLista, 1:usuario, 2:clave
     */
    private void insertMode(String... params) {
        String res="", clave="";
        lista = -1;
        usuario = -1;
        try {
            lista = Integer.valueOf(params[0]);
            clave = URLEncoder.encode(params[1], "UTF-8");
            usuario = Integer.valueOf(params[2]);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        try {
            java.net.URL urlObj = new URL(URL_ADD + LISTA + lista + "&" + USUARIO + usuario + "&" + CLAVE + clave);
            System.out.println(urlObj.toString());
            HttpURLConnection lu = (HttpURLConnection) urlObj.openConnection();

            BufferedReader rd = new BufferedReader(new InputStreamReader(lu.getInputStream()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                res += line;

            }

            rd.close();
            System.out.println("Insert response: " + res);
            JSONObject jo=new JSONObject(res);
            respuesta=jo.getString("success");
            Log.i("ParticipaPers", res);

        } catch (IOException e) {
            e.printStackTrace();
        } catch(JSONException e2){
            e2.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String s) {
       NegocioChecksum.setChecksum("participa");
        if (accion.equals("insert")) {
            this.lqr.sigueAnadirParticipante(!respuesta.equals("1"),usuario,lista);
        }
    }

    private void jsonParser(String data) {
        Usuario u = null;
        try {
            JSONObject jso = new JSONObject(data);
            JSONArray listas = jso.getJSONArray("participa");
            for (int i = 0; i < listas.length(); i++) {
                JSONObject o = listas.getJSONObject(i);
                Integer idUsr = o.getInt("idUsuario");

                AdaptadorBD abd = new AdaptadorBD(this.contexto);
                abd.open();
                u = abd.obtenerUsuario(idUsr);
                if (u == null) {
                    try {
                        final int N = 1;
                        final CountDownLatch cdl = new CountDownLatch(N);
                        ExecutorService pool = Executors.newFixedThreadPool(N);
                        new UsuarioPers(this.contexto, cdl).executeOnExecutor(pool, idUsr);

                        cdl.await();
                        u = abd.obtenerUsuario(idUsr);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                insertarBD(u.getId(), o.getInt("idLista"), o.getString("activo"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void lanzadorExcepcion() throws Exception {
        throw new Exception("Se ha enviado más de un parámetro en: ParticipaPers");
    }

    private void insertarBD(int idUsr, int idLista, String activo) {
        AdaptadorBD adap = new AdaptadorBD(this.contexto);
        adap.open();
        try {
            adap.insertarParticipa(idUsr, idLista, activo.equals("1"));
        } finally {
            adap.close();
        }
    }
}
