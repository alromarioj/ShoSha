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
import java.util.List;

import es.shosha.shosha.dominio.Item;
import es.shosha.shosha.dominio.Usuario;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

/**
 * Clase que se encarga de obtener los items de la base de datos remota y los añade a la interna.
 * Es una tarea en segundo plano.
 * Como parámetros se le pasan los ids de las listas
 * @author Jesús Iráizoz
 */
public class ItemPers extends AsyncTask<Integer, Void, Void> {
    private final static String URL = "http://shosha.jiraizoz.es/getItems.php?";
    private final static String ATRIBUTO = "lista=";

    private Context contexto;

    public ItemPers(Context c) {
        this.contexto = c;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        List<Item> lItems = null;

        String data = "";
        Usuario usu = null;
        if (params.length > 0) {
            for (int s : params) {


                try {
                    data = URLEncoder.encode(String.valueOf(s), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                try {
                    java.net.URL urlObj = new URL(ItemPers.URL + ItemPers.ATRIBUTO + data);

                    HttpURLConnection lu = (HttpURLConnection) urlObj.openConnection();

                    BufferedReader rd = new BufferedReader(new InputStreamReader(lu.getInputStream()));
                    String line = "", res = "";
                    while ((line = rd.readLine()) != null) {
                        res += line;
                    }
                    rd.close();
                    jsonParser(res, s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    private void lanzadorExcepcion() throws Exception {
        throw new Exception("Error en el envio de parámetros en: ItemPers");
    }

    private void jsonParser(String data, int idLista) {

        try {
            JSONObject jso = new JSONObject(data);
            if (jso.has("item")) {
                JSONArray listas = jso.getJSONArray("item");
                for (int i = 0; i < listas.length(); i++) {
                    JSONObject o = listas.getJSONObject(i);

                    Item itm = new Item();
                    itm.setId(o.getInt("id"));
                    itm.setNombre(o.getString("nombre"));
                    itm.setPrecio(o.getDouble("precio"));


                    insertarBD(itm, idLista);


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void insertarBD(Item i, int idLista) {
        AdaptadorBD adap = new AdaptadorBD(this.contexto);
        adap.open();
        try {
            long l = adap.insertarItem(i.getId(), i.getNombre(), i.getPrecio(), idLista);
        } finally {
            adap.close();
        }
    }
}
