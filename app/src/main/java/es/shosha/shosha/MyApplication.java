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
    private static String token;

    public static Usuario getUser() {

        if (user == null) {

            SharedPreferences pref = getAppContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            int id = pref.getInt("idUsuario", -1);

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
        editor.putInt("idUsuario", MyApplication.user.getId());
        editor.apply();
        //Añadir token a la base de datos con el usuario asignado
//        new TokenPers(context).execute("insert",token,String.valueOf(user.getId()));
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        MyApplication.token = token;
    }

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }


    public static Context getAppContext() {
        return MyApplication.context;
    }
}