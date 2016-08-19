package com.jordanleex13.hangman;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.jordanleex13.hangman.Helpers.FragmentHelper;

public class ActivityOnePlayer extends AppCompatActivity {

    private static final String TAG = ActivityOnePlayer.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_player);

        Toolbar toolBar = (Toolbar) findViewById(R.id.activity_one_player_toolbar);

        if (toolBar != null) {
            toolBar.setTitle("One Player");
            toolBar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolBar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        Fragment newFragment = FragmentOnePlayerLogin.newInstance();
        FragmentHelper.swapFragments(getSupportFragmentManager(), R.id.activity_one_player_container,
                newFragment, true, false, null, FragmentOnePlayerLogin.TAG);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Control all back press functionality from Activity rather than individual fragments
     */
    @Override
    public void onBackPressed() {

        String fragTag = getCurrentFragmentTag();

        if (fragTag.equals(FragmentOnePlayerGamePlay.TAG)) {
            Log.e(TAG, "Blocked back-press");
        } else if (fragTag.equals(FragmentOnePlayerLogin.TAG)){
            super.onBackPressed();
        } else if (fragTag.equals(FragmentUserStats.TAG)) {
            super.onBackPressed();
        } else {
                Log.d(TAG, "Unknown back press : " + fragTag);
        }

    }

    // if a fragment is currently being rendered in the placeholder, return its tag
    private String getCurrentFragmentTag() {

        String currFragmentTag = null;

        // find the fragment currently hosted in the fragment placeholder
        Fragment currFragment = getSupportFragmentManager()
                .findFragmentById(R.id.activity_one_player_container);

        // if the fragment isn't null, return it's associated tag
        if (currFragment != null) {

            currFragmentTag = currFragment.getTag();

        } else {

            // create a debug message indicating no fragment is currently hosted
            if (BuildConfig.DEBUG) Log.d(TAG, "Current fragment is NULL");
        }

        return currFragmentTag;
    }
}
