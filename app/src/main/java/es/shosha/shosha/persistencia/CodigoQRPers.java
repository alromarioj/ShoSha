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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

import es.shosha.shosha.MyApplication;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

/**
 * Created by Jesús Iráizoz on 09/05/2017.
 */
public class CodigoQRPers extends AsyncTask<String, Void, Void> {
    private final static String URL_GET = "http://shosha.jiraizoz.es/getCodigoQR.php?";
    private final static String ATRIBUTO_USR = "usuario=";

    //TODO: Obtener todos los códigos QR de la BD remota
    //TODO: Insertar un código QR
    //TODO: Eliminar un código QR

    @Override
    protected Void doInBackground(String[] params) {

        String data = "";
        java.net.URL urlObj = null;
        Map<String, String> mapa = null;

        if (params.length == 1) {
            try {
                data = URLEncoder.encode(params[0].toString(), "UTF-8");
                urlObj = new URL(CodigoQRPers.URL_GET + CodigoQRPers.ATRIBUTO_USR + data);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                urlObj = new URL(CodigoQRPers.URL_GET);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        try {

            HttpURLConnection lu = (HttpURLConnection) urlObj.openConnection();

            BufferedReader rd = new BufferedReader(new InputStreamReader(lu.getInputStream()));
            String line = "", res = "";
            while ((line = rd.readLine()) != null) {
                res += line;
            }

            rd.close();

            mapa = jsonParser(res);
            insertarBD(mapa);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Map<String, String> jsonParser(String data) {

        Map<String, String> mapa = new TreeMap<String, String>();
        try {
            JSONObject jso = new JSONObject(data);
            JSONArray listas = jso.getJSONArray("codigos");
            for (int i = 0; i < listas.length(); i++) {
                JSONObject o = listas.getJSONObject(i);

                mapa.put(o.getString("idLista"), o.getString("codigoQR"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mapa;
    }

    private void insertarBD(Map<String, String> m) {
        AdaptadorBD abd = new AdaptadorBD(MyApplication.getAppContext());
        abd.open();
        for (String s : m.keySet()) {
            abd.insertaQR(m.get(s), Integer.valueOf(s));
        }
        abd.close();
    }
}
