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

import es.shosha.shosha.dominio.Item;
import es.shosha.shosha.dominio.Usuario;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

/**
 * Created by Jesús Iráizoz on 07/03/2017.
 */

public class ItemPers extends AsyncTask<String, Void, Void> {
    private final static String URL = "http://shosha.jiraizoz.es/getItems.php?";
    private final static String ATRIBUTO = "lista=";

    private Context contexto;

    public ItemPers(Context c) {
        this.contexto = c;
    }

    @Override
    protected Void doInBackground(String... params) {
        List<Item> lItems = null;

        String data = "";
        Usuario usu = null;
        if (params.length == 1) {
            try {
                data = URLEncoder.encode(params[0], "UTF-8");
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
                jsonParser(res,params[0]);
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

    private void lanzadorExcepcion() throws Exception {
        throw new Exception("Se ha enviado más de un parámetro en: ItemPers");
    }

    private void jsonParser(String data,String idLista) {
      //  List<Item> lItems = new ArrayList<Item>();
        try {
            JSONObject jso = new JSONObject(data);
            JSONArray listas = jso.getJSONArray("item");
            for (int i = 0; i < listas.length(); i++) {
                JSONObject o = listas.getJSONObject(i);

                Item itm = new Item();
                itm.setId(o.getString("id"));
                itm.setNombre(o.getString("nombre"));
                itm.setPrecio(o.getDouble("precio"));

                insertarBD(itm,idLista);

                //lItems.add(itm);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void insertarBD(Item i, String idLista) {
        AdaptadorBD adap = new AdaptadorBD(this.contexto);
        adap.open();
        try {
            adap.insertarItem(i.getId(), i.getNombre(), i.getPrecio(), idLista);
        } finally {
            adap.close();
        }
    }
}
