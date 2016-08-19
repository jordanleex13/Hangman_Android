package com.jordanleex13.hangman;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.jordanleex13.hangman.Helpers.DatabaseHelper;
import com.jordanleex13.hangman.Helpers.FragmentHelper;
import com.jordanleex13.hangman.Models.CategoryData;

import java.util.ArrayList;

/**
 * Fragment to display a user's performance in the various categories.
 * Utilizes recycler view to display data. Different users can be switched via a spinner
 */
public class FragmentUserStats extends Fragment implements AdapterView.OnItemSelectedListener {

    public static final String TAG = FragmentUserStats.class.getSimpleName();

    // UI
    private Spinner mSpinner;
    private RecyclerView mRecyclerView;

    // Adapters
    private ArrayAdapter<String> mSpinnerAdapter;
    private RVAdapterUserStats mRVAdapter;

    private String mCurrName;
    private DatabaseHelper mDatabaseHelper;

    // ArrayList for a user's stats
    private ArrayList<CategoryData> mCurrUserData = new ArrayList<>();

    public static FragmentUserStats newInstance(String name) {
        FragmentUserStats fragment = new FragmentUserStats();
        Bundle args = new Bundle();
        args.putString("username", name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mDatabaseHelper = new DatabaseHelper(getActivity());

        // If there was a name selected in Login, then load the same name here
        mCurrName = getArguments().getString("username");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_stats, container, false);

        mSpinner = (Spinner) v.findViewById(R.id.fragment_user_stats_spinner_username);

        // create and link the adapter to the spinner object
        mSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, ActivityOnePlayer.mUsers);
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(this);

        // The spinner is 0 based, like an arraylist which is why this works
        // This loads a call to onItemSelected where the recycler view will be populated
        mSpinner.setSelection(ActivityOnePlayer.mUsers.indexOf(mCurrName));

        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_user_stats_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRVAdapter = new RVAdapterUserStats(mCurrUserData);
        mRecyclerView.setAdapter(mRVAdapter);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentHelper.setUpActionBar(getActivity(), true, "User Statistics");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseHelper.close();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.user_stats_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete_user:
                Log.e(TAG, "Deleting user with name " + mCurrName);
                deleteUser(mCurrName);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String selectedName = parent.getItemAtPosition(position).toString();
        Log.d(TAG, "On item selected : " + selectedName);

        mCurrName = selectedName;
        updateRecyclerView(mCurrName);

        easterEggs(selectedName);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.e(TAG, "Error. User should have been created before entering this page");
        returnToPrevious();
    }


    private void returnToPrevious() {
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }


    /**
     * Generates arraylist of user stats for the recycler view
     * @param name      the name of user
     * @return          arraylist of all the user's stats
     */
    private ArrayList<CategoryData> generateCategoryArrayList(String name) {

        int userId = mDatabaseHelper.getUserIdFromName(name);

        ArrayList<CategoryData> arrayList = new ArrayList<>();

        Cursor c = mDatabaseHelper.getReadableDatabase().rawQuery(
                "SELECT * FROM userStats WHERE userId = ?", new String[]{String.valueOf(userId)}
        );

        if (c != null && c.moveToFirst()) {

            String category = "Total";
            int wins = c.getInt(c.getColumnIndex("totalWins"));
            int losses = c.getInt(c.getColumnIndex("totalLosses"));

            CategoryData first = new CategoryData(category,wins, losses);
            arrayList.add(first);

            for (int i = 0; i < CategoryData.CATEGORY_LIST.length; i++) {

                category = CategoryData.CATEGORY_LIST[i];
                wins = c.getInt(c.getColumnIndex(CategoryData.CATEGORY_LIST[i] + "Wins"));
                losses = c.getInt(c.getColumnIndex(CategoryData.CATEGORY_LIST[i] + "Losses"));

                CategoryData temp = new CategoryData(category, wins, losses);
                arrayList.add(temp);
            }

            c.close();
        }
        return arrayList;
    }


    /**
     * Method to delete a user's stats and a user from the database
     * @param name      name of the user to be deleted
     */
    private void deleteUser(String name) {

        for (int i = 0; i< ActivityOnePlayer.mUsers.size(); ++i) {
            Log.d(TAG, "Name : " + ActivityOnePlayer.mUsers.get(i) + "\t ID: " + mDatabaseHelper.getUserIdFromName(ActivityOnePlayer.mUsers.get(i)));
        }

        int userId = mDatabaseHelper.getUserIdFromName(name);

        // delete the user's exercise (session) data
        mDatabaseHelper.getWritableDatabase().delete(
                "userStats", "userId = " + userId, null);

        // delete the user's injury data
        mDatabaseHelper.getWritableDatabase().delete(
                "users", "_id = " + userId, null);

        ActivityOnePlayer.mUsers.remove(name);
        mSpinnerAdapter.notifyDataSetChanged();

        if (ActivityOnePlayer.mUsers.isEmpty()) {
            Log.e(TAG, "No users left. Returning to previous fragment");
            returnToPrevious();
        } else {
            mCurrName = mSpinner.getSelectedItem().toString();
            updateRecyclerView(mCurrName);
        }

    }


    /**
     * Updates the recycler view with the user's stats.
     * @param name      the name of the current user listed in the spinner
     */
    private void updateRecyclerView(String name) {

        mCurrUserData = generateCategoryArrayList(name);

        mRVAdapter.updateArrayList(mCurrUserData);
        mRVAdapter.notifyDataSetChanged();
    }


    private void easterEggs(String selectedItem) {

        if(selectedItem.equals("Ash")) {
            Toast.makeText(getActivity(), "GOTTA CATCH EM ALL", Toast.LENGTH_LONG).show();
        }
    }

}
