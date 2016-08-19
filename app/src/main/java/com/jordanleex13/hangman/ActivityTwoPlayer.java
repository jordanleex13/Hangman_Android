package com.jordanleex13.hangman;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.jordanleex13.hangman.Helpers.FragmentHelper;

public class ActivityTwoPlayer extends AppCompatActivity {

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
}
