package es.shosha.shosha;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import es.shosha.shosha.AdaptadorLista.*;
import es.shosha.shosha.dominio.*;
import es.shosha.shosha.dominio.Lista;

import java.util.ArrayList;

public class ListasActivas extends AppCompatActivity {

    private ListView list;
    //private String[] listas={"Navidad", "Casa", "Cena 28/02/2017"};
    ArrayList<Lista> listas=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        listas.add(new Lista("Navidad", "8 participantes"));
        listas.add(new Lista("Casa", "4 participantes"));
        listas.add(new Lista("Cena 28/02/2017", "10 participantes"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listas_activas);
        list=(ListView)findViewById(R.id.listasActivas);
        //ArrayAdapter<String> adaptador=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listas);
        AdapterLista adaptador=new AdapterLista(this,listas);
        list.setAdapter(adaptador);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Acción a ejecutar al seleccionar elemento de la lista
                Intent i = new Intent(ListasActivas.this, ListaProductos.class);
                i.putExtra("lista",listas.get(position));//Pasa la lista de productos a la actividad
                startActivity(i);
            }
        });
        //Aparece el botón de atrás
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Mostrar menú para las listas activas
        getMenuInflater().inflate(R.menu.menu_listas_activas,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.anadir_lista:
                //Muestra el menú para crear una lista
                Intent i = new Intent(this, AnadirLista.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
