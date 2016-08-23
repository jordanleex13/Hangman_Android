package com.jordanleex13.hangman;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.jordanleex13.hangman.Helpers.FragmentHelper;

public class ActivityTwoPlayerGamePlay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player_game_play);

        Toolbar toolBar = (Toolbar) findViewById(R.id.activity_two_player_gameplay_toolbar);

        if (toolBar != null) {
            toolBar.setTitle("Gameplay");
            toolBar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolBar);

        }

        String word = getIntent().getStringExtra("word");
        int playerNum = getIntent().getIntExtra("playerNum", 0);

        if (word == null || word.isEmpty()) {
            finish();
        } else {
            FragmentTwoPlayerGamePlay newFragment = FragmentTwoPlayerGamePlay.newInstance(word, playerNum);
            FragmentHelper.swapFragments(getSupportFragmentManager(), R.id.activity_two_player_gameplay_container,
                    newFragment, true, false, null, FragmentTwoPlayerGamePlay.TAG);
        }


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

    }
}
