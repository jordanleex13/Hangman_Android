package com.jordanleex13.hangman;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jordanleex13.hangman.Helpers.BitmapHelper;
import com.jordanleex13.hangman.Helpers.FileHelper;
import com.jordanleex13.hangman.Helpers.FragmentHelper;
import com.jordanleex13.hangman.Helpers.PrefUtils;
import com.jordanleex13.hangman.Threads.RunnableCacheBitmap;

public class ActivityMain extends AppCompatActivity {

    public static final String TAG = ActivityMain.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add in the title fragment
        FragmentTitle newFragment = FragmentTitle.newInstance();
        FragmentHelper.swapFragments(getSupportFragmentManager(), R.id.activity_main_container,
                newFragment, true, false, null, FragmentTitle.TAG);

        startCachingImages();

        // TODO eventually switch to false
        PrefUtils.setDefaultPreferences(this, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_settings:
                Log.d(TAG, "Settings pressed");
                break;

            default:
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method that checks if bitmap is in cache and if not, spawns thread to which cache the image
     */
    private void startCachingImages() {

        for (int i = 0; i < BitmapHelper.NUM_OF_HANGMAN_STAGES; i++) {
            if (BitmapHelper.getBitmapFromMemCache(String.valueOf(i)) == null) {
                int resId = FileHelper.getStringIdentifier(this, "stage" + i, "drawable");
                new Thread(new RunnableCacheBitmap(this, resId, String.valueOf(i))).start();
            } else {
                Log.d(TAG, "Already cached stage" + String.valueOf(i));
            }

        }
    }
}
