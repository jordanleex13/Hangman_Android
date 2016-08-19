package com.jordanleex13.hangman.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class PrefUtils {

    public static final String PLAYER_ONE_NAME = "playerOneName";
    public static final String PLAYER_TWO_NAME = "playerTwoName";

    public static void setStringPreference(Context c, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getStringPreference(Context c, String key) {
        String value = "";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        value = prefs.getString(key, null);
        return value;
    }

    public static void setIntPreference(Context c, String key, int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getIntPreference(Context c, String key) {
        int value = -1;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        value = prefs.getInt(key, -1);
        return value;
    }

    public static void setDefaultPreferences(Context c) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(PLAYER_ONE_NAME, null);
        editor.putString(PLAYER_TWO_NAME, null);

        editor.apply();
    }

}
