package es.shosha.shosha;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import es.shosha.shosha.dominio.Lista;

public class ListaProductos extends AppCompatActivity {
    private Lista lista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_productos);
        this.lista=(Lista)this.getIntent().getExtras().getSerializable("lista");//Se recoge la lista que se ha pasado desde ListasActivas
        //Cambia el título de la página que muestra la lista de productos
        final Toolbar tb = (Toolbar) this.findViewById(R.id.toolbar2);
        tb.setTitle(lista.getNombre());
        //Aparece el botón de atrás
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Mostrar menú para la lista de productos
        getMenuInflater().inflate(R.menu.menu_lista_productos,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder;
        View viewInflated;

        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.anadir_producto:
                //Se crea el PopUp para añadir un nuevo producto
                builder=new AlertDialog.Builder(this);
                builder.setTitle("Añadir nuevo producto");

                viewInflated = LayoutInflater.from(getBaseContext()).inflate(R.layout.nuevo_producto, (ViewGroup) findViewById(android.R.id.content), false);
                // Set up the input
                final EditText input_np1 = (EditText) viewInflated.findViewById(R.id.in_nombre_producto);
                //final EditText input_pp = (EditText) viewInflated.findViewById(R.id.in_precio_producto);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(viewInflated);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //Comprobar campos
                        //Añadir producto
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            case R.id.editar_producto:
                //Se crea el PopUp para añadir un nuevo producto
                builder=new AlertDialog.Builder(this);
                builder.setTitle("Editar producto");

                viewInflated = LayoutInflater.from(getBaseContext()).inflate(R.layout.nuevo_producto, (ViewGroup) findViewById(android.R.id.content), false);
                // Set up the input
                final EditText input_np2 = (EditText) viewInflated.findViewById(R.id.in_nombre_producto);
                input_np2.setText("Tomates");
                //final EditText input_pp = (EditText) viewInflated.findViewById(R.id.in_precio_producto);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(viewInflated);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //Comprobar campos
                        //Añadir producto
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
