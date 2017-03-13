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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.shosha.shosha.MyApplication;
import es.shosha.shosha.dominio.Lista;
import es.shosha.shosha.dominio.Usuario;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

/**
 * Created by Jesús Iráizoz on 02/03/2017.
 */
public class ListaPers extends AsyncTask<String, Void, List<Lista>> {
    private final static String URL = "http://shosha.jiraizoz.es/getListas.php?";
    private final static String ATRIBUTO = "usuario=";
    private List<Lista> lListas = null;

    private Context contexto;
    private final CountDownLatch count;

    public ListaPers(Context c, CountDownLatch cdl) {
        this.contexto = c;
        count = cdl;
    }

    @Override
    protected List<Lista> doInBackground(String... params) {
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
                lListas = jsonParser(res);

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
        this.lListas = lListas;
        return lListas;
    }

    protected void onPostExecute(List<Lista> listas) {
        this.lListas = listas;
    }

    private List<Lista> jsonParser(String data) {

        Usuario u = null;
        Lista l = null;
        try {
            JSONObject jso = new JSONObject(data);
            JSONArray listas = jso.getJSONArray("listas");
            for (int i = 0; i < listas.length(); i++) {
                JSONObject o = listas.getJSONObject(i);

                l = new Lista();
                l.setId(o.getString("id"));
                l.setNombre(o.getString("nombre"));
                l.setEstado(o.getString("estado").equals("1"));

                AdaptadorBD abd = new AdaptadorBD(this.contexto);
                abd.open();
                u = abd.obtenerUsuario(o.getString("propietario"));
                if (u != null)
                    l.setPropietario(u);
                else {
                    try {
                        final int N = 1;
                        final CountDownLatch count = new CountDownLatch(N);
                        ExecutorService pool = Executors.newFixedThreadPool(N);
                        new UsuarioPers(this.contexto, count).executeOnExecutor(pool, o.getString("propietario"));

                        count.await();
                        l.setPropietario(abd.obtenerUsuario(o.getString("propietario")));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                abd.close();

                insertarBD(l);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*
        List<Lista> lListas = new ArrayList<Lista>();
        AdaptadorBD abd = new AdaptadorBD(MyApplication.getAppContext());
        abd.open();
        try {
            JSONObject jso = new JSONObject(data);
            JSONArray listas = jso.getJSONArray("listas");
            for (int i = 0; i < listas.length(); i++) {
                JSONObject o = listas.getJSONObject(i);
                List<Item> objetos = new ArrayList<>();
                objetos.add(new Item("1", "1", 1));
                List<Usuario> particip = new ArrayList<>();
                particip.add(new Usuario("1", "1", "1"));
                Lista l = new Lista(o.getString("id"), o.getString("nombre"), new Usuario("u3", "x", "x"), true, objetos, particip);
                lListas.add(l);
                this.insertarBD(l);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        return lListas;
    }

    private void lanzadorExcepcion() throws Exception {
        throw new Exception("Se ha enviado más de un parámetro en: ListaPers");
    }

    private void insertarBD(Lista l) {
        AdaptadorBD adap = new AdaptadorBD(this.contexto);
        adap.open();
        try {
            adap.insertarLista(l.getId(), l.getNombre(), l.getPropietario(), l.isEstado() ? "1" : "0");
        } finally {
            adap.close();
        }
    }
}
