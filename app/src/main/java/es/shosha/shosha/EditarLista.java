package es.shosha.shosha;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import es.shosha.shosha.dominio.Lista;
import es.shosha.shosha.persistencia.ListaPers;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

public class EditarLista extends ListaManual {
    private Lista lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdaptadorBD abd = new AdaptadorBD(getBaseContext());
        abd.open();
        this.lista = abd.obtenerLista(this.getIntent().getExtras().getInt("idLista"), MyApplication.getUser().getId());
        abd.close();

        Button bt=(Button)findViewById(R.id.btcrear);
        bt.setText("Editar");
        EditText et=(EditText)findViewById(R.id.nombre);
        et.setText(lista.getNombre());
        //img.setImageBitmap();//Cambiar imagen
    }

    @Override
    public void crearLista(View view) {//Editar lista
        String nombre=((EditText)findViewById(R.id.nombre)).getText().toString();
        String idu=String.valueOf(MyApplication.getUser().getId());
        int id=lista.getId();

        AdaptadorBD abd = new AdaptadorBD(getBaseContext());
        abd.open();
        //No funciona
        new ListaPers(MyApplication.getAppContext(), null).execute("update", String.valueOf(id),idu,nombre);
        abd.updateLista(abd.obtenerLista(id, Integer.valueOf(idu)),nombre);//Añadir clave
        abd.close();
        Toast.makeText(this, "Editando lista ", Toast.LENGTH_SHORT).show();

        //Muestra las listas del usuario
        Intent i = new Intent(this, ListasActivas.class);
        Bundle bundle = new Bundle();
        bundle.putInt("idLista",id);
        i.putExtras(bundle);
        startActivity(i);
    }
}
