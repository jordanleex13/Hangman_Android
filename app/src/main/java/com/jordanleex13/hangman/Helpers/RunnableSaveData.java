package com.jordanleex13.hangman.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * Created by Jordan on 16-08-15.
 */
public class RunnableSaveData implements Runnable {

    private static final String TAG = RunnableSaveData.class.getSimpleName();

    private Context mContext;
    private boolean win;    //true if win, false if lose
    private String mCategory;
    private int mUserId;
    private DatabaseHelper mDatabaseHelper;

    public RunnableSaveData(Context c, boolean result, String category, int id) {
        mContext = c;
        win = result;
        mCategory = category;

        mUserId = id;
        mDatabaseHelper = new DatabaseHelper(mContext);
    }

    @Override
    public void run() {

        ContentValues cv = new ContentValues();

        if (win) {

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

        Log.e(TAG, "Closing runnable");
    }
}
