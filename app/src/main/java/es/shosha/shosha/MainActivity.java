package es.shosha.shosha;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import es.shosha.shosha.dominio.Usuario;
import es.shosha.shosha.persistencia.UsuarioPers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button b = (Button) findViewById(R.id.boton);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //new DataBaseResult().execute();
                UsuarioPers up = new UsuarioPers();
                up.execute("a1");
                try {
                    Usuario u = up.get();
                    System.out.println(u.toString());
                    TextView tv = (TextView) findViewById(R.id.tDatos);
                    tv.setText(u.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
