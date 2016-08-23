package com.jordanleex13.hangman.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class PrefUtils {

    public static final String PLAYER_ONE_NAME = "playerOneName";
    public static final String PLAYER_TWO_NAME = "playerTwoName";
    public static final String PLAYER_ONE_WINS = "playerOneWins";
    public static final String PLAYER_TWO_WINS = "playerTwoWins";
    public static final String PLAYER_ONE_LOSSES = "playerOneLosses";
    public static final String PLAYER_TWO_LOSSES = "playerTwoLosses";
    public static final String CURRENT_TURN = "currentTurn";        // either 1 or 2

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

    public static void setDefaultPreferences(Context c, boolean fullReset) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = prefs.edit();

        if (fullReset) {
            editor.putString(PLAYER_ONE_NAME, null);
            editor.putString(PLAYER_TWO_NAME, null);
            editor.putInt(PLAYER_ONE_WINS, 0);
            editor.putInt(PLAYER_TWO_WINS, 0);
            editor.putInt(PLAYER_ONE_LOSSES, 0);
            editor.putInt(PLAYER_TWO_LOSSES, 0);
        }
        editor.putInt(CURRENT_TURN, 1);

        editor.apply();
    }

}
