package com.jordanleex13.hangman.Helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Hangman";
    public static final int DATABASE_VERSION = 3;


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
                + "countriesWins INTEGER NOT NULL, "
                + "countriesLosses INTEGER NOT NULL, "
                + "moviesWins INTEGER NOT NULL, "
                + "moviesLosses INTEGER NOT NULL, "
                + "literatureWins INTEGER NOT NULL, "
                + "literatureLosses INTEGER NOT NULL, "
                + "FOREIGN KEY(userId) REFERENCES users(_id)"
                + ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("database", "Updating database");
        if (oldVersion == 1) {
            if (newVersion == 2) {
                insertNewCategoryColumns(db, "countries");
            } else if (newVersion == 3) {
                insertNewCategoryColumns(db, "countries");
                insertNewCategoryColumns(db, "movies");
                insertNewCategoryColumns(db, "literature");
            }
        } else if (oldVersion == 2) {
            if (newVersion == 3) {
                insertNewCategoryColumns(db, "movies");
                insertNewCategoryColumns(db, "literature");
            }
        }
    }

    /**
     * Method to be used onUpgrade to add new categories to existing users
     * Is not user specific since everyone will start off with 0 wins/losses in the new category
     *
     * REMEMBER TO update insertToUserStatsTable in FragmentUserCreation so new users are created with
     * the new categories as well
     *
     * @param db        the writeable database
     * @param category  the new category which will be inserted into userStats
     */
    private void insertNewCategoryColumns(SQLiteDatabase db, String category) {

        // Sanity check
        category = category.toLowerCase();

        String winColumn = category + "Wins";
        String loseColumn = category + "Losses";

        String upgradeQuery1 = "ALTER TABLE userStats ADD COLUMN " + winColumn + " INTEGER NOT NULL DEFAULT 0";
        String upgradeQuery2 = "ALTER TABLE userStats ADD COLUMN " + loseColumn + " INTEGER NOT NULL DEFAULT 0";

        db.execSQL(upgradeQuery1);
        db.execSQL(upgradeQuery2);
    }


    /**
     * Utility method that returns the userId associated with a username
     * @param name      the username
     * @return          the userId, returns 0 if unable to find anything
     */
    public int getUserIdFromName(String name) {

        // Sanity check
        if (name == null) {
            return 0;
        }

        Cursor tempCursor = getReadableDatabase().rawQuery(
                "SELECT _id FROM users WHERE name = ?", new String[]{name});
        tempCursor.moveToFirst();

        int userId = tempCursor.getInt(0);

        tempCursor.close();

        return userId;
    }
}
