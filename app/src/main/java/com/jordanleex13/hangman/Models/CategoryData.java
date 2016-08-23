package com.jordanleex13.hangman.Models;

/**
 * Data structure to deal with a user's performance in a category
 */
public class CategoryData {

    public static final String ANIMALS = "animals";
    public static final String SCIENCE = "science";
    public static final String SPORTS = "sports";
    public static final String PEOPLE = "people";
    public static final String LANDMARKS = "landmarks";
    public static final String POKEMON = "pokemon";

    public static final String[] CATEGORY_LIST = {ANIMALS, SCIENCE, SPORTS, PEOPLE, LANDMARKS, POKEMON};
    public static final int NUM_OF_CATEGORIES = 6;

    private String mCategory;
    private int mWins;
    private int mLosses;
    private double mWinRate;

    public CategoryData(String name, int wins, int losses) {
        mCategory = name;
        mWins = wins;
        mLosses = losses;
        mWinRate = calculateWinRate();
    }

    public static double staticCalculateWinRate(int wins, int losses) {
        if (wins == 0)
            return 0d;
        else if (losses == 0)
            return 1d;
        else
            return ((double) wins) / (wins + losses);
    }

    private double calculateWinRate() {
        if (mWins == 0) {
            return 0d;
        } else if (mLosses == 0) {
            return 1d;
        } else {
            return ((double) mWins) / (mWins + mLosses);
        }
    }
    public String getCategoryName() {
        return mCategory;
    }
    public int getWins() {
        return mWins;
    }
    public int getLosses() {
        return mLosses;
    }
    public double getWinRate() {
        return mWinRate;
    }
}
