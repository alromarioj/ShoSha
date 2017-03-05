package es.shosha.shosha;


import android.os.Bundle;
import android.support.design.widget.NavigationView;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListasActivas extends AppCompatActivity {

    private ListView list;
    private String[] listas={"Navidad", "Casa", "Cena 28/02/2017"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listas_activas);
        list=(ListView)findViewById(R.id.listasActivas);
        ArrayAdapter<String> adaptador=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listas);
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
