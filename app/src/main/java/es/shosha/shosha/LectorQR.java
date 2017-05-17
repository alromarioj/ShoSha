package es.shosha.shosha;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;

import es.shosha.shosha.persistencia.ParticipaFB;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;
import es.shosha.shosha.zxing.IntentIntegrator;
import es.shosha.shosha.zxing.IntentResult;

public class LectorQR extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_lector_qr);
            // verifico si el usuario dio los permisos para la camara
            if (ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Button bt = (Button) findViewById(R.id.btEscanear);
                bt.setOnClickListener(this);


            } else {
                Toast.makeText(getBaseContext(), "La aplicación necesita usar la cámara", Toast.LENGTH_LONG).show();
            }
            //Aparece el botón de atrás
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } catch (Exception e) {
            Log.e(e.getMessage(), e.getLocalizedMessage());
            Toast.makeText(MyApplication.getAppContext(), e.getMessage(), Toast.LENGTH_LONG);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {

            Gson gson = new Gson();

            String clave = gson.fromJson(scanResult.getContents(), String.class);
            Toast.makeText(this, "Código detectado: " + clave, Toast.LENGTH_LONG).show();

            int idu = MyApplication.getUser().getId();

            //new ParticipaPers(MyApplication.getAppContext(), (CountDownLatch) null).execute("insert", "qr", clave, String.valueOf(idu));
            AdaptadorBD abd = new AdaptadorBD(getBaseContext());
            abd.open();
            int lid = abd.obtenerIdListaQR(clave);
            abd.close();
            ParticipaFB.insertaParticipaFB(idu, lid);


            /*try {
//                JSONObject obj = new JSONObject(contents);
//                String lista = obj.getString("idLista");//datos.get("idLista");


                int idu = MyApplication.getUser().getId();

                // Añadir al usuario como participante en la lista
                //Comprobar clave
                AdaptadorBD abd = new AdaptadorBD(getBaseContext());
                abd.open();
                int idl = Integer.valueOf(lista);
                Lista l = abd.obtenerLista(idl, idu);

                if (clave == l.getCodigoQR()) {
                    //Añade participante en bd remota

                    //Añade participante en bd local
                    abd.insertarParticipa(idu, Integer.valueOf(lista), true);


                    Toast.makeText(this, "Lista añadida", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(this, ListasActivas.class);//Va al apartado de listas activas
                    startActivity(i);
                } else {
                    Toast.makeText(this, "Error al añadir la lista", Toast.LENGTH_SHORT).show();
                    startActivityForResult(data, 0);//Reinicia el escáner
                }
                abd.close();
            } catch (JSONException e) {
                e.printStackTrace();
            }*/


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private HashMap<String, String> obtenerDatos(String query) {
        HashMap<String, String> mapa = new HashMap<>();
        String[] datos = query.split("&");
        String[] par;
        for (int i = 0; i < datos.length; i++) {
            par = datos[i].split("=");
            mapa.put(par[0], par[1]);
        }
        return mapa;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btEscanear) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.addExtra("SCAN_MODE", "QR_CODE_MODE");
            integrator.initiateScan();
        }
    }
}
/*Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.setPackage("com.google.zxing.client.android");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);*/