package es.shosha.shosha.persistencia;



import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by inhernan on 18/05/2017.
 */

public class Notificacion extends AsyncTask<String, Void, Void> {
    private final static String URLCompra="http://shosha.jiraizoz.es/notificarCompra.php";
    public Notificacion(){

    }
    @Override
    protected Void doInBackground(String... params) {
        try{
            List<String> usuarios=new ArrayList<>();
            for(int i=1;i<params.length;i++){
                usuarios.add(params[i]);
            }
            send(params[0],usuarios);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static void send(String idLista, List usuarios) throws IOException {
        URL url = new URL(URLCompra);
        Map<String, Object> params = new LinkedHashMap<>();

        params.put("lista", idLista);
        params.put("usuarios",usuarios);

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0)
                postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        for (int c = in.read(); c != -1; c = in.read())
            System.out.print((char) c);
    }

}
