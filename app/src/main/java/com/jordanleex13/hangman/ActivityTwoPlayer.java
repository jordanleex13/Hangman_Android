package com.jordanleex13.hangman;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.jordanleex13.hangman.Helpers.FragmentHelper;

public class ActivityTwoPlayer extends AppCompatActivity {

    private static final String TAG = ActivityTwoPlayer.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player);

        Toolbar toolBar = (Toolbar) findViewById(R.id.activity_two_player_toolbar);

        if (toolBar != null) {
            toolBar.setTitle("Two Player");
            toolBar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolBar);
        }

        Fragment newFragment = FragmentTwoPlayerLogin.newInstance();
        FragmentHelper.swapFragments(getSupportFragmentManager(), R.id.activity_two_player_container,
                newFragment, true, false, null, FragmentTwoPlayerLogin.TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        //Control all back press functionality from Activity rather than individual fragments

        String fragTag = getCurrentFragmentTag();
        Log.e(TAG, fragTag);
//        if (fragTag.equals(FragmentOnePlayerGamePlay.TAG)) {
//            Log.e(TAG, "Blocked back-press");
//        } else if (fragTag.equals(FragmentOnePlayerLogin.TAG)){
//            super.onBackPressed();
//        } else if (fragTag.equals(FragmentUserStats.TAG)) {
//            super.onBackPressed();
//        } else {
//            Log.d(TAG, "Unknown back press : " + fragTag);

        super.onBackPressed();

    }

    // if a fragment is currently being rendered in the placeholder, return its tag
    private String getCurrentFragmentTag() {

        String currFragmentTag = null;

        // find the fragment currently hosted in the fragment placeholder
        Fragment currFragment = getSupportFragmentManager()
                .findFragmentById(R.id.activity_two_player_container);

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

