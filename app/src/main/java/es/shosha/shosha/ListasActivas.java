package es.shosha.shosha;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import es.shosha.shosha.AdaptadorLista.AdapterLista;
import es.shosha.shosha.dominio.Lista;
import es.shosha.shosha.persistencia.ListaPers;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

public class ListasActivas extends AppCompatActivity {

    private ListView list;
    private Lista listaClicada;
    //private String[] listas={"Navidad", "Casa", "Cena 28/02/2017"};
    List<Lista> listas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // obtener listas del usuario

        AdaptadorBD abd = new AdaptadorBD(getBaseContext());
        abd.open();

        listas = abd.getListas(MyApplication.getUser().getId());
        Log.d("lis", listas.toString());

        setListas(listas);

        abd.close();

     /*   try {
            AsyncTask<String, Void, List<Lista>> at = new ListaPers().execute("u1");
            setListas(at.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/
        // para cada una, la metemos al array listas
        /*listas.add(new Lista("Navidad", "8 participantes"));
        listas.add(new Lista("Casa", "4 participantes"));
        listas.add(new Lista("Cena 28/02/2017", "10 participantes"));*/
        super.onCreate(savedInstanceState);

    }

    public void setListas(List<Lista> listas) {
        setContentView(R.layout.activity_listas_activas);
        list = (ListView) findViewById(R.id.listasActivas);
        //ArrayAdapter<String> adaptador=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listas);
        final AdapterLista adaptador = new AdapterLista(this, listas);
        list.setAdapter(adaptador);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(ListasActivas.this, ListaProductos.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("lista", adaptador.getItem(position));
                i.putExtras(bundle);
                startActivity(i);
            }
        });
        // Registramos el menu contextual
        registerForContextMenu(list);
        //Aparece el botón de atrás
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Mostrar menú para las listas activas?
        getMenuInflater().inflate(R.menu.menu_listas_activas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    /**
     * Se crea el Context Menu, añadiendo las opciones deseadas
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ListView lv = (ListView) v;
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle("Opciones");
        menu.add(0, v.getId(), 0, "Eliminar");
        listaClicada = (Lista) lv.getItemAtPosition(acmi.position);
    }

    /**
     * Se definen las funciones que se llamarán al pulsar las
     * opciones del Context Menu
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Eliminar") {
            function1(listaClicada.getId());
        } else {
            return false;
        }
        return true;
    }

    /**
     * Se definen las acciones que se realizan al pulsar la Accion1 en
     * del Context Menu.
     * En este caso, como ejemplo, damos un mensaje en pantalla
     */
    public void function1(String id) {
        AdaptadorBD abd = new AdaptadorBD(getBaseContext());
        abd.open();
        new ListaPers(MyApplication.getAppContext(), null).execute("delete", id, MyApplication.getUser().getId());
        abd.eliminarLista(id, MyApplication.getUser());
        listas = abd.getListas(MyApplication.getUser().getId());
        setListas(listas);
        abd.close();
        Toast.makeText(this, "Eliminando lista " + id, Toast.LENGTH_SHORT).show();
    }
}
