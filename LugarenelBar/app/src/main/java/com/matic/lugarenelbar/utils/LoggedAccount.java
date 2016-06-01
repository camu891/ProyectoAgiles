package com.matic.lugarenelbar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by blueriver on 21/05/2016.
 */
public class LoggedAccount {

    public static String getLoggedAccount(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString("username", "");
    }

    public static void clearLoggedAccount(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("username");
        editor.commit();
    }

    public static void setLoggedAccount(String username, Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", username);
        editor.commit();
    }
}
