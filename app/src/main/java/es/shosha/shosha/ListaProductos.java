package es.shosha.shosha;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import es.shosha.shosha.AdaptadorLista.AdapterProductos;
import es.shosha.shosha.dominio.Item;
import es.shosha.shosha.dominio.Lista;


import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

public class ListaProductos extends AppCompatActivity {
    private ListView list;
    private Lista lista;
    private List<Item> productos=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.lista=(Lista)this.getIntent().getExtras().getSerializable("lista");//Se recoge la lista que se ha pasado desde ListasActivas
        productos=lista.getItems();
        setContentView(R.layout.activity_lista_productos);

        list = (ListView) findViewById(R.id.listaProductos);

        final AdapterProductos adaptador = new AdapterProductos(this, productos);
        list.setAdapter(adaptador);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        //Cambia el título de la página que muestra la lista de productos
        final Toolbar tb = (Toolbar) findViewById(R.id.toolbar2);
        tb.setTitle(lista.getNombre());
        //Aparece el botón de atrás
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        super.onCreate(savedInstanceState);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Mostrar menú para la lista de productos
        getMenuInflater().inflate(R.menu.menu_lista_productos,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.anadir_producto:
                //Se crea el PopUp para añadir un nuevo producto
                AlertDialog.Builder builder;
                View viewInflated;
                builder=new AlertDialog.Builder(this);
                builder.setTitle("Añadir nuevo producto");

                viewInflated = LayoutInflater.from(getBaseContext()).inflate(R.layout.nuevo_producto, (ViewGroup) findViewById(android.R.id.content), false);
                // Set up the input
                final EditText input_np1 = (EditText) viewInflated.findViewById(R.id.in_nombre_producto);
                final EditText input_pp = (EditText) viewInflated.findViewById(R.id.in_precio_producto);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(viewInflated);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Asumiendo que el precio es >=0
                        AdaptadorBD abd = new AdaptadorBD(getBaseContext());
                        abd.open();
                        //new ItemPers(MyApplication.getAppContext()).execute("insert", id, MyApplication.getUser().getId());
                        //Se inserta un producto a la lista a partir de los datos introducidos
                        String precio=input_pp.getText().toString();
                        precio=(precio.isEmpty()?"0":precio);
                        abd.insertarItem(String.valueOf(lista.getItems().size()),input_np1.getText().toString(),Double.valueOf(precio),lista.getId());
                        abd.close();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                return true;
            case R.id.editar_producto:
                AlertDialog.Builder builder1;
                //Se crea el PopUp para añadir un nuevo producto
                builder1=new AlertDialog.Builder(this);
                builder1.setTitle("Editar producto");

                View viewInflated1 = LayoutInflater.from(getBaseContext()).inflate(R.layout.nuevo_producto, (ViewGroup) findViewById(android.R.id.content), false);
                // Set up the input
                final EditText input_np2 = (EditText) viewInflated1.findViewById(R.id.in_nombre_producto);
                input_np2.setText("Tomates");
                //final EditText input_pp = (EditText) viewInflated.findViewById(R.id.in_precio_producto);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder1.setView(viewInflated1);

                // Set up the buttons
                builder1.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //Comprobar campos
                        //Añadir producto
                    }
                });
                builder1.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder1.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
