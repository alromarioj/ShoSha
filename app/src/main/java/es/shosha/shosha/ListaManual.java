package es.shosha.shosha;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ListaManual extends AppCompatActivity {
    private static final int SELECT_PICTURE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_manual);
        //Aparece el botón de atrás
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    public void abrirGaleria(View v){

        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(i,"Seleccione una imagen"),SELECT_PICTURE);
    }
    //Recibe la foto elegida
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        if (requestCode == SELECT_PICTURE) {
            Uri selectedImage = imageReturnedIntent.getData();
            InputStream is;
            try {
                is = getContentResolver().openInputStream(selectedImage);
                BufferedInputStream bis = new BufferedInputStream(is);
                Bitmap bitmap = BitmapFactory.decodeStream(bis);
                //Cambia la imagen por la seleccionada
                ImageView img = (ImageView)findViewById(R.id.imagen);
                img.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {}

        }

    }
}

