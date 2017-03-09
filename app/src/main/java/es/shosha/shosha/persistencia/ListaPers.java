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
import java.util.ArrayList;
import java.util.List;

import es.shosha.shosha.dominio.Lista;
import es.shosha.shosha.dominio.Usuario;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

/**
 * Created by Jesús Iráizoz on 02/03/2017.
 */
public class ListaPers extends AsyncTask<String, Void, Void> {
    private final static String URL = "http://shosha.jiraizoz.es/getListas.php?";
    private final static String ATRIBUTO = "usuario=";
    private Context contexto;

    public ListaPers(Context c) {
        this.contexto = c;
    }


    @Override
    protected Void doInBackground(String... params) {
        List<Lista> lListas = null;

        String data = "";
        Usuario usu = null;
        if (params.length == 1) {
            try {
                data = URLEncoder.encode(params[0], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                java.net.URL urlObj = new URL(ListaPers.URL + ListaPers.ATRIBUTO + data);

                HttpURLConnection lu = (HttpURLConnection) urlObj.openConnection();

                BufferedReader rd = new BufferedReader(new InputStreamReader(lu.getInputStream()));
                String line = "", res = "";
                while ((line = rd.readLine()) != null) {
                    res += line;
                }

                rd.close();
                jsonParser(res);
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
        List<Lista> lListas = new ArrayList<Lista>();
        try {
            JSONObject jso = new JSONObject(data);
            JSONArray listas = jso.getJSONArray("listas");
            for (int i = 0; i < listas.length(); i++) {
                JSONObject o = listas.getJSONObject(i);

                Lista l = new Lista();
                l.setId(o.getString("id"));
                l.setNombre(o.getString("nombre"));

                l.setEstado(o.getString("estado").equals("1"));

                insertarBD(l,o.getString("propietario"));

                //lListas.add(l);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void lanzadorExcepcion() throws Exception {
        throw new Exception("Se ha enviado más de un parámetro en: ListaPers");
    }

    private void insertarBD(Lista l, String idProp) {
        AdaptadorBD adap = new AdaptadorBD(this.contexto);
        adap.open();
        try {
            adap.insertarLista(l.getId(), l.getNombre(), idProp, l.isEstado() ? "1" : "0");
        } finally {
            adap.close();
        }
    }

}
