package com.jordanleex13.hangman.Helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Hangman";
    public static final int DATABASE_VERSION = 1;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE users ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "age TEXT NOT NULL"
                + ");");

        db.execSQL("CREATE TABLE userStats ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "userId INTEGER NOT NULL, "
                + "totalWins INTEGER NOT NULL, "
                + "totalLosses INTEGER NOT NULL, "
                + "animalsWins INTEGER NOT NULL, "
                + "animalsLosses INTEGER NOT NULL, "
                + "scienceWins INTEGER NOT NULL, "
                + "scienceLosses INTEGER NOT NULL, "
                + "sportsWins INTEGER NOT NULL, "
                + "sportsLosses INTEGER NOT NULL, "
                + "peopleWins INTEGER NOT NULL, "
                + "peopleLosses INTEGER NOT NULL, "
                + "landmarksWins INTEGER NOT NULL, "
                + "landmarksLosses INTEGER NOT NULL, "
                + "pokemonWins INTEGER NOT NULL, "
                + "pokemonLosses INTEGER NOT NULL, "
                + "FOREIGN KEY(userId) REFERENCES users(_id)"
                + ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void insertNewCategoryColumns(SQLiteDatabase db, String category) {

        String winColumn = category + "_wins";
        String loseColumn = category + "_losses";

        String upgradeQuery1 = "ALTER TABLE users ADD COLUMN " + winColumn + " INTEGER NOT NULL DEFAULT 0";
        String upgradeQuery2 = "ALTER TABLE users ADD COLUMN " + loseColumn + " INTEGER NOT NULL DEFAULT 0";

        db.execSQL(upgradeQuery1);
        db.execSQL(upgradeQuery2);
    }
}
