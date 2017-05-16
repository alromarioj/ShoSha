package es.shosha.shosha;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class faq extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        TextView textFAQ = (TextView) findViewById(R.id.textFAQ);
        textFAQ.setMovementMethod(new ScrollingMovementMethod());
        String welcomStr=getString(R.string.textoFAQ);
        textFAQ.setText(Html.fromHtml(welcomStr));
        Button volver = (Button) findViewById(R.id.buttonVolver);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
