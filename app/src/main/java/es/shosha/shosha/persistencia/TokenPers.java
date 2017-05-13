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

import es.shosha.shosha.ListaProductos;
import es.shosha.shosha.MyApplication;
import es.shosha.shosha.dominio.Item;
import es.shosha.shosha.negocio.NegocioChecksum;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

/**
 * Clase que se encarga de obtener los items de la base de datos remota y los añade a la interna.
 * Es una tarea en segundo plano.
 * Como parámetros se le pasan los ids de las listas
 *
 * @author Jesús Iráizoz
 */
public class TokenPers extends AsyncTask<String, Void, Void> {
    private final static String URL_ADD = "http://shosha.jiraizoz.es/addToken.php?";
    private final static String TOKEN = "token=";
    private final static String USUARIO = "usuario=";

    private Context contexto;

    public TokenPers(Context c) {
        this.contexto = c;
    }

    @Override
    protected Void doInBackground(String... params) {
        String data = "";
        if (params.length == 3 && params[0].equals("insert")) {
            insertMode(params[1], params[2]);
        } else {
            try {
                lanzadorExcepcion();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Añade un producto a una lista
     *
     * @param params 0:idLista, 1:nombre, 2:precio, 3:cantidad
     */
    private void insertMode(String... params) {
        String token = "",
                usuario = "";
        try {
            token = URLEncoder.encode(params[0], "UTF-8");
            usuario = URLEncoder.encode(params[1], "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            java.net.URL urlObj = new URL(TokenPers.URL_ADD + TOKEN + token + "&" + USUARIO + usuario);
            HttpURLConnection lu = (HttpURLConnection) urlObj.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(lu.getInputStream()));
            String line = "", res = "";
            while ((line = rd.readLine()) != null) {
                res += line;
            }
            rd.close();

            Log.i("--> URL insercion token", urlObj.toString());
            System.out.println("Insert remote response: " + res);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void lanzadorExcepcion() throws Exception {
        throw new Exception("Error en el envio de parámetros en: TokenPers");
    }

}
