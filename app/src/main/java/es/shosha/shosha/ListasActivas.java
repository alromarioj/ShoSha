package es.shosha.shosha;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import es.shosha.shosha.AdaptadorLista.AdapterLista;
import es.shosha.shosha.AdaptadorLista.Lista;

public class ListasActivas extends AppCompatActivity {

    private ListView list;
    //private String[] listas={"Navidad", "Casa", "Cena 28/02/2017"};
    ArrayList<Lista> listas=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // obtener listas del usuario

        // para cada una, la metemos al array listas
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
            }
        });
        //Aparece el botón de atrás
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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
            case R.id.anadir:
                //Muestra el menú para crear una lista
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
