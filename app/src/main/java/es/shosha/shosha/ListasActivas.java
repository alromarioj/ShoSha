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
    List<Lista> listas = new ArrayList<>();
    AdapterLista adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // obtener listas del usuario

        AdaptadorBD abd = new AdaptadorBD(getBaseContext());
        abd.open();

        listas = abd.obtenerListas(MyApplication.getUser().getId());
        Log.d("lis", listas.toString());

        setContentView(R.layout.activity_listas_activas);
        list = (ListView) findViewById(R.id.listasActivas);
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

        super.onCreate(savedInstanceState);

    }

    public void setListas(List<Lista> listas) {
        adaptador = new AdapterLista(this, listas);
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
        //Mostrar menú para las listas activas
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
        menu.add(0, v.getId(),0, "Cambiar nombre");
        listaClicada = (Lista) lv.getItemAtPosition(acmi.position);
    }

    /**
     * Se definen las funciones que se llamarán al pulsar las
     * opciones del Context Menu
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String opcion=item.getTitle().toString();
        if (opcion == "Eliminar") {
            //Se elimina la lista seleccionada de las listas del usuario
            int id=listaClicada.getId();
            AdaptadorBD abd = new AdaptadorBD(getBaseContext());
            abd.open();
            new ListaPers(MyApplication.getAppContext(), null).execute("delete", String.valueOf(id), MyApplication.getUser().getStringId());
            abd.eliminarLista(id, MyApplication.getUser());
            listas.remove(listaClicada);
            adaptador.notifyDataSetChanged();

            abd.close();
            Toast.makeText(this, "Eliminando lista " + id, Toast.LENGTH_SHORT).show();
        }
        else if(opcion=="Cambiar nombre"){
            //Mostrar popup para cambiar el nombre de la lista
        }
        else{
            return false;
        }
        return true;
    }
}
