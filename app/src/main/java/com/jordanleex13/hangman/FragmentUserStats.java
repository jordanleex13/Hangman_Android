package com.jordanleex13.hangman;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.jordanleex13.hangman.Helpers.DatabaseHelper;
import com.jordanleex13.hangman.Models.CategoryData;

import java.util.ArrayList;

/**
 * Fragment to display a user's performance in the various categories.
 * Utilizes recycler view to display data. Different users can be switched via a spinner
 */
public class FragmentUserStats extends Fragment implements AdapterView.OnItemSelectedListener {

    public static final String TAG = FragmentUserStats.class.getSimpleName();

    private Spinner mSpinner;
    private ArrayAdapter<String> mSpinnerAdapter;
    private ArrayList<String> mUsers = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RVAdapterUserStats mRVAdapter;

    private int mUserId;
    private DatabaseHelper mDatabaseHelper;

    private ArrayList<CategoryData> mCurrUserData;

    public static FragmentUserStats newInstance(ArrayList<String> users, int userId) {
        FragmentUserStats fragment = new FragmentUserStats();
        Bundle args = new Bundle();
        args.putStringArrayList("userList", users);
        args.putInt("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUsers = getArguments().getStringArrayList("userList");
        mDatabaseHelper = new DatabaseHelper(getActivity());

        // Just load the first user
        mCurrUserData = generateCategoryArrayList(1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_stats, container, false);

        mSpinner = (Spinner) v.findViewById(R.id.fragment_user_stats_spinner_username);

        // create and link the adapter to the spinner object
        mSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mUsers);
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(this);

        int initialId = getArguments().getInt("userId");

        // The spinner is 0 based
        mSpinner.setSelection(initialId-1);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_user_stats_recycler);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRVAdapter = new RVAdapterUserStats(mCurrUserData);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mRVAdapter);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("User Statistics");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        mDatabaseHelper.close();
        super.onDestroy();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String selectedItem = parent.getItemAtPosition(position).toString();
        Log.e(TAG, selectedItem);

        // Position 0 in the spinner corresponds to userId 1 in the database
        System.out.println(position + 1);

        mCurrUserData = generateCategoryArrayList(position+1);
        mRVAdapter.updateArrayList(mCurrUserData);
        mRVAdapter.notifyDataSetChanged();

        easterEggs(selectedItem);

    }
    private void easterEggs(String selectedItem) {

        if(selectedItem.equals("Ash")) {
            Toast.makeText(getActivity(), "GOTTA CATCH EM ALL", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.e(TAG, "Error. User should have been created before entering this page");
        returnToPrevious();
    }

    private void returnToPrevious() {
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }


    private ArrayList<CategoryData> generateCategoryArrayList(int userId) {

        Log.e(TAG, "USER ID: " + String.valueOf(userId));
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

}
