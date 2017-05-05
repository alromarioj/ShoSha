package es.shosha.shosha;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Random;

import es.shosha.shosha.persistencia.ListaPers;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

import static es.shosha.shosha.R.id.nombre;

public class ListaManual extends AppCompatActivity {
    private String nomLista, claveLista;
    private static final int SELECT_PICTURE=1;
    protected ImageView img;
    private final String ruta_fotos= Environment.getExternalStorageDirectory().getAbsolutePath()+"ShoSha/imagenes";
    private File file=new File(ruta_fotos);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_manual);
        img=(ImageView)findViewById(R.id.imagen);
        //Aparece el botón de atrás
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        file.mkdirs();
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
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == SELECT_PICTURE)
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    //lblPhoto.setText(getPath(selectedImage));
                    img.setImageURI(selectedImage);

                }
    }
    public void crearLista(View view){
        this.nomLista=((EditText)findViewById(nombre)).getText().toString();
        this.claveLista = generarClave(5);
        String idu=String.valueOf(MyApplication.getUser().getId());

        new ListaPers(MyApplication.getAppContext(), null, this).execute("insert", idu,nomLista,claveLista,"imagen");
    }
    public void sigueCrearLista(Integer id){
        System.out.println("=?="+id);
        AdaptadorBD abd = new AdaptadorBD(getBaseContext());
        abd.open();
        abd.insertarLista(id,nomLista,MyApplication.getUser(),"1");//Añadir clave
        abd.insertaQR(claveLista,id);
        abd.close();
        Toast.makeText(this, "Añadiendo lista ", Toast.LENGTH_SHORT).show();

        //Muestra las listas del usuario
        Intent i = new Intent(this, ListaProductos.class);
        Bundle bundle = new Bundle();
        bundle.putInt("idLista",id);
        i.putExtras(bundle);
        startActivity(i);
        this.finish();
    }
    private String generarClave(int longitud){
        String cadenaAleatoria = "";
        long milis = new java.util.GregorianCalendar().getTimeInMillis();
        Random r = new Random(milis);
        int i = 0;
        while ( i < longitud){
            char c = (char)r.nextInt(255);
            if ( (c >= '0' && c <='9') || (c >='A' && c <='Z') ){
                cadenaAleatoria += c;
                i ++;
            }
        }
        return cadenaAleatoria;
    };
}

