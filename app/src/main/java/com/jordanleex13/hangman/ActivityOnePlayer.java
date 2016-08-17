package com.jordanleex13.hangman;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.jordanleex13.hangman.Helpers.FragmentHelper;

public class ActivityOnePlayer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_player);

        Toolbar toolBar = (Toolbar) findViewById(R.id.activity_one_player_toolbar);

        if (toolBar != null) {
            toolBar.setTitle("One Player");
            toolBar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolBar);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setHomeButtonEnabled(true);
        }

        Fragment newFragment = FragmentOnePlayerLogin.newInstance();
        FragmentHelper.swapFragments(getSupportFragmentManager(), R.id.activity_one_player_container,
                newFragment, true, false, null, FragmentOnePlayerLogin.TAG);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
