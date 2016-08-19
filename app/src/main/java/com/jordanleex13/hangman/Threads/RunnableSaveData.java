package com.jordanleex13.hangman.Threads;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.jordanleex13.hangman.Helpers.DatabaseHelper;

/**
 * Runnable that saves post-game data to the database for a specific user
 */
public class RunnableSaveData implements Runnable {

    private static final String TAG = RunnableSaveData.class.getSimpleName();

    private boolean win;            // true if win, false if lose
    private String mCategory;       // the category the user just played from
    private int mUserId;            // the user's ID
    private DatabaseHelper mDatabaseHelper;

    public RunnableSaveData(Context c, boolean result, String category, int id) {
        win = result;
        mCategory = category;
        mUserId = id;
        mDatabaseHelper = new DatabaseHelper(c);
    }

    @Override
    public void run() {

        ContentValues cv = new ContentValues();

        if (win) {

            // If the user wins, update the totalWins and category specific wins

            String columnName = mCategory.toLowerCase() + "Wins";
            String query = "SELECT totalWins, " + columnName + " FROM userStats WHERE userId = ?";

            Cursor winCursor = mDatabaseHelper.getWritableDatabase().rawQuery(
                    query, new String[] {String.valueOf(mUserId)}
            );
            winCursor.moveToFirst();
            cv.put("totalWins", winCursor.getInt(0) + 1);
            cv.put(columnName, winCursor.getInt(1) + 1);
            winCursor.close();

        } else {

            // If the user loses, update the totalLosses and category specific losses

            String columnName = mCategory.toLowerCase() + "Losses";
            String query = "SELECT totalLosses, " + columnName + " FROM userStats WHERE userId = ?";

            Cursor loseCursor = mDatabaseHelper.getWritableDatabase().rawQuery(
                    query, new String[] {String.valueOf(mUserId)}
            );
            loseCursor.moveToFirst();
            cv.put("totalLosses", loseCursor.getInt(0) + 1);
            cv.put(columnName, loseCursor.getInt(1) + 1);
            loseCursor.close();
        }


        mDatabaseHelper.getWritableDatabase().update("userStats", cv,
                "userId = ?", new String[]{String.valueOf(mUserId)});

        mDatabaseHelper.close();
        Log.e(TAG, "Closing runnable");
    }
}
