package es.shosha.shosha;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


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

                try{
                    String decodificado= URLDecoder.decode(contents, "UTF-8");
                    JSONObject obj = new JSONObject(decodificado);
                    String lista=obj.getString("idLista");
                    String clave=obj.getString("clave");
                    int idu=MyApplication.getUser().getId();

                    // Añadir al usuario como participante en la lista
                    //Añade participante en bd remota
                    new ParticipaPers(MyApplication.getAppContext(), null,this).execute("insert", lista,clave,String.valueOf(idu));

                }
                catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }
    public void sigueAnadirParticipante(boolean error, int usuario, int lista){
        System.out.println("Error: "+error+" Usuario: "+usuario+" Lista: "+lista);
        if(error){
            Toast.makeText(this, "Error al añadir la lista", Toast.LENGTH_SHORT).show();
        }
        else{
            AdaptadorBD abd = new AdaptadorBD(getBaseContext());
            abd.open();
            //Añade participante en bd local
            abd.insertarParticipa(usuario, lista,true);

            Toast.makeText(this, "Lista añadida", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, ListaProductos.class);//Muestra la lista nueva
            Bundle bundle = new Bundle();
            bundle.putInt("idLista", lista);
            i.putExtras(bundle);
            startActivity(i);
            abd.close();
        }
    }
    public void abrirEscaner(View view){
        // verifico si el usuario dio los permisos para la camara
        if (ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } else {
            Toast.makeText(getBaseContext(), "La aplicación necesita usar la cámara", Toast.LENGTH_LONG).show();
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
}
