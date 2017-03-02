package es.shosha.shosha;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListasActivas extends Activity {
    private ListView list;
    private String[] listas={"Navidad", "Casa", "Cena 28/02/2017"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        super.onCreate(savedInstanceState);

    }
}
