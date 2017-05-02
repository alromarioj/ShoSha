package es.shosha.shosha;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

public class LectorQR extends AppCompatActivity {
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    SurfaceView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector_qr);

        Intent i = new Intent("com.google.zxing.client.android.SCAN");
        i.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(i, 0);

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

                try {
                    String decodificado=URLDecoder.decode(contents, "UTF-8");
                    HashMap<String,String> datos=obtenerDatos(decodificado);
                    String lista=datos.get("idLista");
                    String clave=datos.get("clave");
                    Toast.makeText(this, "Código detectado", Toast.LENGTH_LONG).show();

                    // Añadir al usuario como participante en la lista
                    //Añade participante en bd remota

                    /*if(){
                        //Añade participante en bd local

                        Toast.makeText(this, "Lista añadida", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(this, ListasActivas.class);//Va al apartado de listas activas
                        startActivity(i);
                    }
                    else{
                        Toast.makeText(this, "Error al añadir la lista", Toast.LENGTH_SHORT).show();
                        startActivityForResult(data, 0);//Reinicia el escáner
                    }*/

                }
                catch (UnsupportedEncodingException e){
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
