package com.jordanleex13.hangman.Helpers;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Fully static class with methods to assist in file and resource IO
 */
public class FileHelper {

    public static String selectRandomWord(Context c, int resId) {

        BufferedReader br = null;
        List<String> mWordList = new ArrayList<>();

        try {

            String sCurrentLine;

            br = new BufferedReader(new InputStreamReader(c.getResources().openRawResource(resId)));

            while ((sCurrentLine = br.readLine()) != null) {
                mWordList.add(sCurrentLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (!mWordList.isEmpty()) {
            Random random = new Random();
            return mWordList.get(random.nextInt(mWordList.size()));
        }
        return null;
    }

    // Used to get resource ID
    public static int getStringIdentifier(Context context, String name, String folder) {
        return context.getResources().getIdentifier(name, folder,
                context.getPackageName());
    }
}
