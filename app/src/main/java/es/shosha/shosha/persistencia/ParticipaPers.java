package es.shosha.shosha.persistencia;

import android.content.Context;
import android.os.AsyncTask;

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

import es.shosha.shosha.dominio.Usuario;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

/**
 * Created by Jesús Iráizoz on 13/03/2017.
 */

public class ParticipaPers extends AsyncTask<Integer, Void, Void> {
    private final static String URL = "http://shosha.jiraizoz.es/getParticipaciones.php?";
    private final static String ATRIBUTO = "lista=";

    private Context contexto;
    private final CountDownLatch count;

    public ParticipaPers(Context contexto, CountDownLatch count) {
        this.contexto = contexto;
        this.count = count;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        String data = "";
        Usuario usu = null;
        if (params.length == 1) {
            try {
                data = URLEncoder.encode(params[0].toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                java.net.URL urlObj = new URL(ParticipaPers.URL + ParticipaPers.ATRIBUTO + data);

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
        } else {
            try {
                lanzadorExcepcion();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
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
