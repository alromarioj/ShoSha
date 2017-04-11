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

import es.shosha.shosha.dominio.Usuario;

/**
 * Created by Jesús Iráizoz on 04/04/2017.
 */

public class ChecksumPers extends AsyncTask<String, Void, Map<String, Double>> {
    private final static String URL = "http://shosha.jiraizoz.es/getChecksum.php";
    private final static String ATRIBUTO = "?tabla=";

    @Override
    protected Map<String, Double> doInBackground(String[] params) {

        String data = "";
        java.net.URL urlObj = null;
        Map<String,Double> mapa = null;

        if (params.length == 1) {
            try {
                data = URLEncoder.encode(params[0].toString(), "UTF-8");
                urlObj = new URL(ChecksumPers.URL + ChecksumPers.ATRIBUTO + data);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                urlObj = new URL(ChecksumPers.URL);
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

        } catch (IOException e) {
            e.printStackTrace();
        }

        return mapa;
    }

    private Map<String, Double> jsonParser(String data) {

        Map<String, Double> mapa = new TreeMap<String, Double>();
        try {
            JSONObject jso = new JSONObject(data);
            JSONArray listas = jso.getJSONArray("chk");
            for (int i = 0; i < listas.length(); i++) {
                JSONObject o = listas.getJSONObject(i);

                mapa.put(o.getString("tabla"),o.getDouble("crc"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mapa;
    }
}
