package es.shosha.shosha;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import es.shosha.shosha.dominio.Lista;
import es.shosha.shosha.dominio.Usuario;
import es.shosha.shosha.persistencia.ListaPers;
import es.shosha.shosha.persistencia.UsuarioPers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

     /*   UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, "DBUsuarios", null, 1);     // <--------------------------------------------------------------
        final SQLiteDatabase db = usdbh.getWritableDatabase();*/

        Button b = (Button) findViewById(R.id.boton);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //new DataBaseResult().execute();


                ListaPers lp = new ListaPers();
                lp.execute("a1");

                TextView tv = (TextView) findViewById(R.id.tDatos);



                UsuarioPers up = new UsuarioPers();
                up.execute("a1");
                try {

                    List<Lista> lList = lp.get();

                    for(Lista l : lList) {
                        System.out.println(l.toString());
                    }

                    Usuario u = up.get();
                    System.out.println(u.toString());
                    tv.setText(u.toString());

              /*      if(db!=null){
                        db.execSQL("INSERT INTO Usuarios VALUES ('"+u.getId()+"','"+u.getNombre()+"')");
                    }

                    Cursor c = db.query("Usuarios",null,null,null,null,null,null);
                    tv.setText(c.getString(0));
                    db.close();*/
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }



            }
        });
    }
}
