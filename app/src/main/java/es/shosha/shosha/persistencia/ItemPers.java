package es.shosha.shosha.persistencia;

import android.app.Activity;
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
import java.util.List;
import java.util.Map;

import es.shosha.shosha.ListaProductos;
import es.shosha.shosha.MyApplication;
import es.shosha.shosha.dominio.Item;
import es.shosha.shosha.dominio.Usuario;
import es.shosha.shosha.negocio.NegocioChecksum;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

/**
 * Clase que se encarga de obtener los items de la base de datos remota y los añade a la interna.
 * Es una tarea en segundo plano.
 * Como parámetros se le pasan los ids de las listas
 *
 * @author Jesús Iráizoz
 */
public class ItemPers extends AsyncTask<String, Void, Void> {
    private final static String URL = "http://shosha.jiraizoz.es/getItems.php?";
    private final static String URL_ADD = "http://shosha.jiraizoz.es/addItem.php?";
    private final static String URL_DEL = "http://shosha.jiraizoz.es/delItem.php?";
    private final static String URL_UPD = "http://shosha.jiraizoz.es/updateItem.php?";
    private final static String ATRIBUTO = "lista=";
    private final static String ID = "producto=";
    private final static String USUARIO="usuario=";
    private final static String NOMBRE = "nombre=";
    private final static String PRECIO = "precio=";
    private final static String CANTIDAD = "cantidad=";

    private Context contexto;
    private ListaProductos lp;
    private int item;
    private String accion="";

    public ItemPers(Context c) {
        this.contexto = c;
    }
    public ItemPers(Context c, ListaProductos lp) {
        this.contexto = c;
        this.lp=lp;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        NegocioChecksum.setChecksum("item");
        if (accion.equals("insert")) {
            this.lp.sigueNuevoProducto(item);
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        String data = "";
        if (params.length > 0) {
            if (params.length == 5 && params[0].equals("insert")) {
                accion=params[0];
                insertMode(params[1], params[2], params[3], params[4]);
            } else if (params.length == 3 && params[0].equals("delete")) {
                deleteMode(params[1], params[2]);
            } else if (params.length == 4 && params[0].equals("buy")) {
                buyMode(params[1], params[2],params[3]);//lista+producto+usuario
            }else if (params.length == 6 && params[0].equals("update")) {
                updateMode(params[1], params[2], params[3], params[4], params[5]);
            } else {
                for (String s : params) {
                    try {
                        data = URLEncoder.encode(s, "UTF-8");
                        java.net.URL urlObj = new URL(ItemPers.URL + ItemPers.ATRIBUTO + data);

                        HttpURLConnection lu = (HttpURLConnection) urlObj.openConnection();

                        BufferedReader rd = new BufferedReader(new InputStreamReader(lu.getInputStream()));
                        String line = "", res = "";
                        while ((line = rd.readLine()) != null) {
                            res += line;
                        }
                        rd.close();
                        jsonParser(res, Integer.valueOf(s));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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


    /**
     * Añade un producto a una lista
     *
     * @param params 0:idLista, 1:nombre, 2:precio, 3:cantidad
     */
    private void insertMode(String... params) {
        String idLista = "",
                nombre = "";
        double precio = 0;
        int cantidad = 1;
        try {
            idLista = URLEncoder.encode(params[0], "UTF-8");
            nombre = URLEncoder.encode(params[1], "UTF-8");
            precio = Double.valueOf(params[2]);
            cantidad = Integer.valueOf(params[3]);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            java.net.URL urlObj = new URL(ItemPers.URL_ADD + ItemPers.ATRIBUTO + idLista + "&" + ItemPers.NOMBRE + nombre + "&" + ItemPers.PRECIO + precio + "&" + ItemPers.CANTIDAD + cantidad);

            HttpURLConnection lu = (HttpURLConnection) urlObj.openConnection();

            BufferedReader rd = new BufferedReader(new InputStreamReader(lu.getInputStream()));
            String line = "", res = "";
            while ((line = rd.readLine()) != null) {
                res += line;
            }

            rd.close();

            Log.i("--> URL insercion lista", urlObj.toString());

            AdaptadorBD abd = new AdaptadorBD(MyApplication.getAppContext());
            abd.open();
            abd.insertarItem(Integer.valueOf(res), nombre, precio, Integer.valueOf(idLista), cantidad, false);
            //Insertar en BD remota

            abd.close();
            item=Integer.valueOf(res);

            System.out.println("Insert remote response: " + res);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Compra un producto de una lista
     *
     * @param params 0:idLista, 1:idProducto, 2:usuario
     */
    private void buyMode(String... params) {
        int idLista = -1,
                idProducto = -1,
                usuario = -1;
        try {
            idLista = Integer.valueOf(params[0]);
            idProducto = Integer.valueOf(params[1]);
            usuario = Integer.valueOf(params[2]);
        }  catch (NumberFormatException e) {
            e.printStackTrace();
        }

        try {
            java.net.URL urlObj = new URL(URL_BUY + ATRIBUTO + idLista + "&" + ID + idProducto + "&" + USUARIO+ usuario);

            HttpURLConnection lu = (HttpURLConnection) urlObj.openConnection();

            BufferedReader rd = new BufferedReader(new InputStreamReader(lu.getInputStream()));
            String line = "", res = "";
            while ((line = rd.readLine()) != null) {
                res += line;
            }

            rd.close();
            Log.i("--> URL compra item",  urlObj.toString());
            AdaptadorBD abd = new AdaptadorBD(MyApplication.getAppContext());
            abd.open();
            long resl = abd.comprarItem(idProducto);
            abd.close();

            System.out.println("Buy local response: " + resl);
            System.out.println("Buy remote response: " + res);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Edita un producto de una lista
     *
     * @param params 0:idLista, 1:idProducto, 2:nombre, 3:precio, 4:cantidad
     */
    private void updateMode(String... params) {
        String nombre = "";
        double precio = 0;
        int idLista = -1,
                idProducto = -1,
                cantidad = 1;
        try {
            idLista = Integer.valueOf(params[0]);
            idProducto = Integer.valueOf(params[1]);
            nombre = URLEncoder.encode(params[2], "UTF-8");
            precio = Double.valueOf(params[3]);
            cantidad = Integer.valueOf(params[4]);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        try {
            java.net.URL urlObj = new URL(URL_UPD + ATRIBUTO + idLista + "&" + ID + idProducto + "&" + NOMBRE + nombre + "&" + PRECIO + precio + "&" + CANTIDAD + cantidad);

            HttpURLConnection lu = (HttpURLConnection) urlObj.openConnection();

            BufferedReader rd = new BufferedReader(new InputStreamReader(lu.getInputStream()));
            String line = "", res = "";
            while ((line = rd.readLine()) != null) {
                res += line;
            }

            rd.close();
            Log.i("--> URL update lista", URL_UPD + ATRIBUTO + idLista + "&" + ID + idProducto + "&" + NOMBRE + nombre + "&" + PRECIO + precio + "&" + CANTIDAD + cantidad);

            System.out.println("Update response: " + res);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Borra un producto de una lista
     *
     * @param params 0:idLista, 1:nombre, 2:precio, 3:cantidad
     */
    private void deleteMode(String... params) {
        String idLista = "",
                producto = "";
        try {
            idLista = URLEncoder.encode(params[0], "UTF-8");
            producto = URLEncoder.encode(params[1], "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {

            java.net.URL urlObj = new URL(ItemPers.URL_DEL + ItemPers.ATRIBUTO + idLista + "&" + ItemPers.ID + producto);

            Log.i(" --> URL borrado lista", ItemPers.URL_DEL + ItemPers.ATRIBUTO + idLista + "&" + ItemPers.ID + producto);

            HttpURLConnection lu = (HttpURLConnection) urlObj.openConnection();

            BufferedReader rd = new BufferedReader(new InputStreamReader(lu.getInputStream()));
            String line = "", res = "";
            while ((line = rd.readLine()) != null) {
                res += line;
            }

            rd.close();


            System.out.println("------------------------> Borrado, va a local");
            System.out.println("------------------------> Borrado, va a local");
            System.out.println("------------------------> Borrado, va a local");

            AdaptadorBD abd = new AdaptadorBD(MyApplication.getAppContext());
            abd.open();
            long resl = abd.eliminarItem(idLista, producto);
            abd.close();

            System.out.println("Delete remote response: " + res);
            System.out.println("Delete local response: " + resl);

        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    itm.setIdLista(o.getInt("idLista"));
                    itm.setCantidad(o.getInt("cantidad"));
                    itm.setComprado(o.getBoolean("comprado"));
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
            long l = adap.insertarItem(i.getId(), i.getNombre(), i.getPrecio(), idLista, i.getCantidad(), i.isComprado());
        } finally {
            adap.close();
        }
    }
}
