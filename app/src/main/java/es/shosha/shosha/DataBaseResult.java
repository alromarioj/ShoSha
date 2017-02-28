package es.shosha.shosha;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Jesús Iráizoz on 28/02/2017.
 */

public class DataBaseResult extends AsyncTask<Void, Void, Void> {

    DataBaseResult() {


    }

    @Override
    protected Void doInBackground(Void... params) {
        String url = "http://shosha.jiraizoz.es/getListas.php?usuario=";
        String data = null;
        try {
            data = URLEncoder.encode("a1", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url += data;

        try {
            URL urlObj = new URL(url);
            //URLConnection lu = urlObj.openConnection();

            HttpURLConnection lu = (HttpURLConnection) urlObj.openConnection();

            // Send data - if you don't need to send data
            // ignore this section and just move on to the next one

       /*     System.out.println(" > " + data);
            lu.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(lu.getOutputStream());
            wr.write(data);
            wr.flush();*/

// Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(lu.getInputStream()));
            String line = "", res = "";
            while ((line = rd.readLine()) != null) {
                res += line;
            }

            //wr.flush();
            rd.close();
            System.out.println(res);
            jsonParser(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String jsonParser(String data) {
        try {
            JSONObject jso = new JSONObject(data);
            JSONArray listas = jso.getJSONArray("listas");
            for(int i =0 ;i<listas.length();i++){
                JSONObject o = listas.getJSONObject(i);
              //  System.out.println(o.toString());
                System.out.println(o.getString("id"));
                System.out.println(o.getString("nombre"));
                System.out.println(o.getString("propietario"));
                System.out.println(o.getString("estado"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "?";
    }
}
