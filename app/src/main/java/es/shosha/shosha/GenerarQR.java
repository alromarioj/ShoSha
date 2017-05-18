package es.shosha.shosha;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


public class GenerarQR extends AppCompatActivity {
    int idLista;
    String clave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generar_qr);
        Bundle blista=this.getIntent().getExtras();
        idLista=blista.getInt("idLista");
        clave=blista.getString("clave");
        String nombre=blista.getString("nombre");
        Toolbar titulo=(Toolbar)findViewById(R.id.toolbar);
        titulo.setTitle(titulo.getTitle()+" "+nombre);
        //Se genera el código QR de la lista
        generar(findViewById(R.id.espacioCodigo));

        //Aparece el botón de atrás
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    public void generar(View view){
        String datos="{'idLista'='"+idLista+"','clave'='"+clave+"'}";
        ImageView qrcode=(ImageView)findViewById(R.id.codigoQR);

        Writer writer = new QRCodeWriter();
        String finaldata = Uri.encode(datos, "utf-8");
        try{
            BitMatrix bm = writer.encode(finaldata, BarcodeFormat.QR_CODE,150, 150);
            Bitmap ImageBitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);

            for (int i = 0; i < 150; i++) {//width
                for (int j = 0; j < 150; j++) {//height
                    ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK: Color.WHITE);
                }
            }

            if (ImageBitmap != null) {
                qrcode.setImageBitmap(ImageBitmap);
            } else {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
        catch (WriterException e){
            e.printStackTrace();
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
