package com.jordanleex13.hangman;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Activity that uses a view pager to display two fragments. A user info fragment and a word selection
 * fragment.
 */
public class ActivityTwoPlayer extends AppCompatActivity {

    private static final String TAG = ActivityTwoPlayer.class.getSimpleName();

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

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

        // Create the adapter that will return a fragment for each of the two sections
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.activity_two_player_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

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


    /**
     * Public method (can be used by fragments) that switches the tab
     * @param viewPageNum
     */
    public void switchViewPagerTab(int viewPageNum) {
        mViewPager.setCurrentItem(viewPageNum);

    }



    // Standard adapter for viewpager
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        // Keep a reference to the fragments
        private Fragment[] fragList;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragList = new Fragment[2];
        }


        @Override
        public Fragment getItem(int position) {

            // Login is the first fragment
            if(position == 0) {
                FragmentTwoPlayerLogin fragment = FragmentTwoPlayerLogin.newInstance();
                fragList[position] = fragment;
                return fragment;

            // Word selection is the second
            } else {
                FragmentWordSelection fragment = FragmentWordSelection.newInstance();

                // set the listener as the "user info" fragment
                fragment.setUserDataInputtedListener(fragList[0]);

                fragList[position] = fragment;
                return fragment;
            }

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return "User Info";
                case 1: return "Word Selection";
            }
            return null;
        }
    }
}

