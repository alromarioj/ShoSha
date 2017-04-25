package es.shosha.shosha;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;

public class GenerarQR extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generar_qr);
        //Aparece el botón de atrás
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    public void generar(View view){
        Bundle bundle = new Bundle();
        bundle.putString("idLista","1");
        bundle.putString("clave","123fb");

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.addExtra("ENCODE_DATA", bundle);
        System.out.println(bundle.toString());
        //integrator.shareText("idL=1&clave=123fb", "TEXT_TYPE");
        integrator.shareText(bundle.toString(), "TEXT_TYPE");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
