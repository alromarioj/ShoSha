package es.shosha.shosha.persistencia;

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

/**
 * Created by Jesús Iráizoz on 12/03/2017.
 */

public class VersionPers extends AsyncTask<String, Void, Long> {

    private final static String URL = "http://shosha.jiraizoz.es/getVersion.php?";
    private final static String ATRIBUTO = "id=";

    @Override
    protected Long doInBackground(String... params) {
        String data = "";

        if (params.length == 1) {
            try {
                data = URLEncoder.encode(params[0], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                java.net.URL urlObj = new URL(VersionPers.URL + VersionPers.ATRIBUTO + data);

                HttpURLConnection lu = (HttpURLConnection) urlObj.openConnection();

                BufferedReader rd = new BufferedReader(new InputStreamReader(lu.getInputStream()));
                String line = "", res = "";
                while ((line = rd.readLine()) != null) {
                    res += line;
                }

                rd.close();
                return jsonParser(res);
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
        return -1L;
    }

    private long jsonParser(String data) {
        try {
            JSONObject jso = new JSONObject(data);
            JSONArray listas = jso.getJSONArray("usuario");
            for (int i = 0; i < listas.length(); i++) {
                JSONObject o = listas.getJSONObject(i);

                return o.getLong("modificacion");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    private void lanzadorExcepcion() throws Exception {
        throw new Exception("Se ha enviado más de un parámetro en: UsuarioPers");
    }
}
