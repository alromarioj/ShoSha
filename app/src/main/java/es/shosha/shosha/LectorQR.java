package es.shosha.shosha;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

import es.shosha.shosha.dominio.Lista;
import es.shosha.shosha.persistencia.ParticipaPers;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

public class LectorQR extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector_qr);
        // verifico si el usuario dio los permisos para la camara
        if (ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Button bt=(Button)findViewById(R.id.btEscanear);
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.setPackage("com.google.zxing.client.android");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } else {
            Toast.makeText(getBaseContext(), "La aplicación necesita usar la cámara", Toast.LENGTH_LONG).show();
        }
        //Aparece el botón de atrás
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0){
            if(resultCode==RESULT_OK){
                String contents=data.getStringExtra("SCAN_RESULT");
                String format=data.getStringExtra("SCAN_RESULT_FORMAT");

                try{
                    JSONObject obj = new JSONObject(contents);
                    String lista=obj.getString("idLista");//datos.get("idLista");
                    String clave=obj.getString("clave");//datos.get("clave");
                    Toast.makeText(this, "Código detectado", Toast.LENGTH_LONG).show();

                    int idu=MyApplication.getUser().getId();

                    // Añadir al usuario como participante en la lista
                    //Comprobar clave
                    AdaptadorBD abd = new AdaptadorBD(getBaseContext());
                    abd.open();
                    int idl=Integer.valueOf(lista);
                    Lista l=abd.obtenerLista(idl,idu);

                    if(clave==l.getClave()){
                        //Añade participante en bd remota
                        new ParticipaPers(MyApplication.getAppContext(), null).execute("insert", lista,String.valueOf(idu),clave);
                        //Añade participante en bd local
                        abd.insertarParticipa(idu, Integer.valueOf(lista),true);


                        Toast.makeText(this, "Lista añadida", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(this, ListasActivas.class);//Va al apartado de listas activas
                        startActivity(i);
                    }
                    else{
                        Toast.makeText(this, "Error al añadir la lista", Toast.LENGTH_SHORT).show();
                        startActivityForResult(data, 0);//Reinicia el escáner
                    }
                    abd.close();
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
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
    private HashMap<String,String> obtenerDatos(String query){
        HashMap<String,String> mapa=new HashMap<>();
        String[] datos=query.split("&");
        String[] par;
        for(int i=0;i<datos.length;i++){
            par=datos[i].split("=");
            mapa.put(par[0],par[1]);
        }
        return mapa;
    }
}
