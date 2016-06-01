package com.matic.lugarenelbar.volley;

/**
 * Created by Usuario on 25/05/2016.
 */

        import android.app.Application;
        import android.content.Context;


public class AppController extends Application {

    private static AppController instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static AppController getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
}
