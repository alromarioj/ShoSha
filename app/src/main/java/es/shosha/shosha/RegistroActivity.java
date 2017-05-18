package es.shosha.shosha;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import es.shosha.shosha.dominio.Usuario;
import es.shosha.shosha.persistencia.UsuarioFB;

import static es.shosha.shosha.R.id.email;

public class RegistroActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mrePasswordView;
    private EditText mNombreView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        // Set up the login form.
        mEmailView = (EditText) findViewById(email);
        mNombreView = (EditText) findViewById(R.id.nombre);

        mPasswordView = (EditText) findViewById(R.id.password);
        mrePasswordView = (EditText) findViewById(R.id.repassword);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignUpButton = (Button) findViewById(R.id.email_sign_up_button);
        mEmailSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        TextView condiciones = (TextView) findViewById(R.id.textView5);
        condiciones.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), Condiciones.class);
                startActivity(myIntent);
            }
        });

        mLoginFormView = findViewById(R.id.email_login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mrePasswordView.setError(null);
        mNombreView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String repassword = mrePasswordView.getText().toString();
        String nombre = mNombreView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(repassword) && !TextUtils.equals(password, repassword)) {
            mrePasswordView.setError(getString(R.string.error_pass_repe));
            focusView = mrePasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(nombre)) {
            mNombreView.setError(getString(R.string.error_field_required));
            focusView = mNombreView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserRegisterTask(nombre, email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mNombre;
        private final String mEmail;
        private final String mPassword;

        UserRegisterTask(String nombre, String email, String password) {
            mNombre = nombre;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Integer success = -10;
            try {
                URL urlObj = new URL("http://shosha.jiraizoz.es/addUsuario.php?nombre=" + URLEncoder.encode(mNombre, "UTF-8") + "&email=" + URLEncoder.encode(mEmail, "UTF-8") + "&pass=" + URLEncoder.encode(mPassword, "UTF-8"));
//                System.out.println("=============================");
//                System.out.println("http://shosha.jiraizoz.es/addUsuario.php?nombre=" + URLEncoder.encode(mNombre, "UTF-8") + "&email=" + URLEncoder.encode(mEmail, "UTF-8") + "&pass=" + URLEncoder.encode(mPassword, "UTF-8"));

                HttpURLConnection lu = (HttpURLConnection) urlObj.openConnection();

                BufferedReader rd = new BufferedReader(new InputStreamReader(lu.getInputStream()));
                String line, res = "";
                while ((line = rd.readLine()) != null) {
                    res += line;
                }
                rd.close();
                try{
                    JSONObject jso = new JSONObject(res);
                    success = jso.getInt("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //TODO: VIGILAR BBDD, puede haber conflicto de id
            UsuarioFB.insertaUsuarioFB(new Usuario(0, mNombre, mEmail), true);

            return success == 1;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Toast.makeText(getBaseContext(), "¡Gracias por registrarte! Ya puedes iniciar sesión",Toast.LENGTH_LONG).show();
                Intent i = new Intent(RegistroActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(i);
            } else {
                Toast.makeText(getBaseContext(), "No se ha podido realizar el registro (Error: "+success+")",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}