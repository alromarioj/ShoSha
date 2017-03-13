package es.shosha.shosha;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import es.shosha.shosha.AdaptadorLista.AdapterLista;
import es.shosha.shosha.dominio.Lista;
import es.shosha.shosha.persistencia.ItemPers;
import es.shosha.shosha.persistencia.ListaPers;
import es.shosha.shosha.persistencia.UsuarioPers;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

public class ListasActivas extends AppCompatActivity {

    private ListView list;
    //private String[] listas={"Navidad", "Casa", "Cena 28/02/2017"};
    List<Lista> listas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ItemPers itempers = new ItemPers(getBaseContext());
        ListaPers listapers = new ListaPers(getBaseContext());
        UsuarioPers usuariopers = new UsuarioPers(getBaseContext());

        AsyncTask<String,Void,List<Lista>> l=listapers.execute("u3");
        usuariopers.execute("u3");

        AdaptadorBD adaptador = new AdaptadorBD(getBaseContext());
        adaptador.open();
        try {
            listas = listapers.execute("u3").get();
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_listas_activas);
            list=(ListView)findViewById(R.id.listasActivas);
            //ArrayAdapter<String> adaptador=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listas);
            AdapterLista adapter=new AdapterLista(this,listas);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
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
            adaptador.close();
        } catch(Exception e){}


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Mostrar menú para las listas activas?
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
                Intent i = new Intent(this, AnadirLista.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
