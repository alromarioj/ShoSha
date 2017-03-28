package es.shosha.shosha;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import es.shosha.shosha.dominio.Usuario;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

/**
 * Created by alvaro on 12/03/2017.
 */

public class MyApplication extends Application {

    private static Context context;
    private static Usuario user;

    public static Usuario getUser() {

        if (user == null) {

            SharedPreferences pref = getAppContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            String id = pref.getString("idUsuario", "");

            AdaptadorBD abd = new AdaptadorBD(getAppContext());
            abd.open();
            setUser(abd.obtenerUsuario(id));
            abd.close();
        }
        return user;
    }

    public static void setUser(Usuario user) {
        MyApplication.user = user;
        SharedPreferences pref = getAppContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("idUsuario", user.getId());
        editor.apply();
    }

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }


    public static Context getAppContext() {
        return MyApplication.context;
    }
}