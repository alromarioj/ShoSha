package es.shosha.shosha;

import android.app.Application;
import android.content.Context;

/**
 * Created by alvaro on 12/03/2017.
 */

public class MyApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}