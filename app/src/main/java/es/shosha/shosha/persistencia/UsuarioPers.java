package es.shosha.shosha.persistencia;

import android.content.Context;
import android.database.Cursor;
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

import es.shosha.shosha.dominio.Usuario;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

/**
 * Created by Jesús Iráizoz on 02/03/2017.
 */

public class UsuarioPers extends AsyncTask<String, Void, Usuario> {
    private final static String URL = "http://shosha.jiraizoz.es/getUsuario.php?";
    private final static String ATRIBUTO = "id=";

    private Context contexto;

    public UsuarioPers(Context c) {
        this.contexto = c;
    }

    @Override
    protected Void doInBackground(String... params) {
        String data = "";
        Usuario usu = null;
        if (params.length == 1) {
            try {
                data = URLEncoder.encode(params[0], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                java.net.URL urlObj = new URL(UsuarioPers.URL + UsuarioPers.ATRIBUTO + data);

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
        Usuario u = null;
        try {
            JSONObject jso = new JSONObject(data);
            JSONArray listas = jso.getJSONArray("usuario");
            for (int i = 0; i < listas.length(); i++) {
                JSONObject o = listas.getJSONObject(i);

                u = new Usuario(o.getString("id"), o.getString("nombre"), o.getString("email"));

                insertarBD(u);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void lanzadorExcepcion() throws Exception {
        throw new Exception("Se ha enviado más de un parámetro en: UsuarioPers");
    }

    private void insertarBD(Usuario u) {
        AdaptadorBD adap = new AdaptadorBD(this.contexto);
        adap.open();
        try {
            adap.insertarUsuario(u.getId(), u.getNombre(), u.getEmail());
        } finally {
            adap.close();
        }
    }

}