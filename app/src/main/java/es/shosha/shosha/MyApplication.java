package es.shosha.shosha;

import android.app.Application;
import android.content.Context;

import es.shosha.shosha.dominio.Usuario;

/**
 * Created by alvaro on 12/03/2017.
 */

public class MyApplication extends Application {

    private static Context context;
    private static Usuario user;

    public static Usuario getUser() {
        return user;
    }

    public static void setUser(Usuario user) {
        MyApplication.user = user;
    }

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}