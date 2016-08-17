package com.jordanleex13.hangman;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.jordanleex13.hangman.Helpers.DatabaseHelper;
import com.jordanleex13.hangman.Helpers.FragmentHelper;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;


public class FragmentOnePlayerLogin extends Fragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, FragmentUserCreation.OnUserCreatedListener {

    public static final String TAG = FragmentOnePlayerLogin.class.getSimpleName();

    private Spinner mUsernameSpinner;
    private ArrayAdapter mAdapter;
    private ArrayList<String> mUsers = new ArrayList<>();

    private RadioGroup cpuDifficulty;
    private RadioButton selectedCpuDifficulty;      // set to medium by default
    private Button startButton;

    private DatabaseHelper mDatabaseHelper;

    private HashMap<String, Boolean> checkedHashMap;
    public static final int NUM_OF_CATEGORIES = 6;
    private CheckBox animals;
    private CheckBox science;
    private CheckBox sports;
    private CheckBox people;
    private CheckBox landmarks;
    private CheckBox pokemon;

    private String selectedCategory;
    private String selectedDifficulty;

    private int checkboxCounter;

    private int mUserId = 0;

    public static FragmentOnePlayerLogin newInstance() {
        FragmentOnePlayerLogin fragment = new FragmentOnePlayerLogin();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "On create");
        setHasOptionsMenu(true);
        mDatabaseHelper = new DatabaseHelper(getActivity());
        checkedHashMap = new HashMap<>(NUM_OF_CATEGORIES);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e(TAG, "oncrEeate Veiw");

        // Reset checkbox counter
        checkboxCounter = 0;

        View v = inflater.inflate(R.layout.fragment_one_player_login, container, false);

        cpuDifficulty = (RadioGroup) v.findViewById(R.id.fragment_one_player_login_cpu_difficulty);
        startButton = (Button) v.findViewById(R.id.fragment_one_player_login_button_start);

        mUsernameSpinner = (Spinner) v.findViewById(R.id.fragment_one_player_login_spinner_username);
        // create and link the adapter to the spinner object
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mUsers);
        mUsernameSpinner.setAdapter(mAdapter);

        animals = (CheckBox) v.findViewById(R.id.fragment_one_player_login_category_animals);
        science = (CheckBox) v.findViewById(R.id.fragment_one_player_login_category_science);
        sports = (CheckBox) v.findViewById(R.id.fragment_one_player_login_category_sports);
        people = (CheckBox) v.findViewById(R.id.fragment_one_player_login_category_people);
        landmarks = (CheckBox) v.findViewById(R.id.fragment_one_player_login_category_landmarks);
        pokemon = (CheckBox) v.findViewById(R.id.fragment_one_player_login_category_pokemon);

        animals.setOnCheckedChangeListener(this);
        science.setOnCheckedChangeListener(this);
        sports.setOnCheckedChangeListener(this);
        people.setOnCheckedChangeListener(this);
        landmarks.setOnCheckedChangeListener(this);
        pokemon.setOnCheckedChangeListener(this);

        startButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "oResume");

//        getActivity().getActionBar().setTitle("One Player");
//        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);

        updateSpinner(false);

        Log.e(TAG, String.valueOf(checkboxCounter));

        checkedHashMap.put("animals", animals.isChecked());
        checkedHashMap.put("science", science.isChecked());
        checkedHashMap.put("sports", sports.isChecked());
        checkedHashMap.put("people", people.isChecked());
        checkedHashMap.put("landmarks", landmarks.isChecked());
        checkedHashMap.put("pokemon", pokemon.isChecked());


    }

    @Override
    public void onPause() {
        Log.e(TAG, "ON PAUSE");
        super.onPause();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.oneplayer_login_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_create_user:

                openCreateUserDialog();

                break;
            case R.id.menu_statistics:
                Log.d(TAG, "Launching Statistics");

                if (checkName()) {
                    Fragment newFragment = FragmentUserStats.newInstance(mUsers, mUserId);
                    FragmentHelper.swapFragments(getActivity().getSupportFragmentManager(),
                            R.id.activity_one_player_container, newFragment,
                            false, true, TAG, FragmentUserStats.TAG);
                }


                break;
            default:
                Log.e(TAG, "Unknown menu click : " + item.getItemId());
                break;
        }

        return true;
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch(id) {
            case R.id.fragment_one_player_login_button_start:

                if(validationCheck()) {

                    Assert.assertTrue(selectedCategory != null && selectedDifficulty != null);

                    Fragment newFragment = FragmentOnePlayerGamePlay.newInstance(selectedCategory, selectedDifficulty, mUserId);
                    FragmentHelper.swapFragments(getActivity().getSupportFragmentManager(),
                            R.id.activity_one_player_container, newFragment,
                            false, true, TAG, FragmentOnePlayerGamePlay.TAG);
                }

                break;
            default:
                Log.e(TAG, "Unexpected click registered");
                break;
        }
    }

    private boolean checkName() {
        String name = mUsernameSpinner.getSelectedItem().toString();

        if (name.isEmpty()) {
            return false;
        } else {
            Cursor cursor = mDatabaseHelper.getReadableDatabase().rawQuery(
                    "SELECT _id FROM users WHERE name = ?", new String[] {name}
            );
            cursor.moveToFirst();

            mUserId = cursor.getInt(0);
            Log.e(TAG, "User name : " + name + "\tUser ID : " + String.valueOf(mUserId));

            return true;

        }
    }
    private boolean validationCheck() {
        boolean readyToStart = true;

        if (!mUsers.isEmpty()) {
            readyToStart = checkName();

        } else {
            readyToStart = false;
        }

        // find the radiobutton by returned id
        int selectedId = cpuDifficulty.getCheckedRadioButtonId();
        if (selectedId != -1) {
            selectedCpuDifficulty = (RadioButton) getView().findViewById(selectedId);
            selectedDifficulty = selectedCpuDifficulty.getText().toString().toLowerCase();
            Log.d(TAG, "Selected difficulty : " + selectedDifficulty);
        } else {
            readyToStart = false;
        }

        if (checkboxCounter == 0) {
            Toast.makeText(getActivity(), "Select at least one category", Toast.LENGTH_SHORT).show();
            readyToStart = false;
        } else {
            // Parse through all checkboxes
            Log.d(TAG, String.valueOf(checkboxCounter) + " categories picked");

            ArrayList<String> isCheckedAl = new ArrayList<>();

            // Iterate through hashmap and store checked values in arraylist
            Iterator it = checkedHashMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();

                Log.d(TAG, pair.getKey() + " = " + pair.getValue());

                if (pair.getValue() == true) {
                    isCheckedAl.add(pair.getKey().toString());
                }
            }

            Log.d(TAG, "Size of arraylist is "+ String.valueOf(isCheckedAl.size()));
            // Select a random number from [0, size) and get value from that index
            Random rand = new Random();
            int n = rand.nextInt(isCheckedAl.size());
            selectedCategory = isCheckedAl.get(n);
        }
        return readyToStart;
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, buttonView.getText().toString().toLowerCase() + "\t" + isChecked);

        checkedHashMap.put(buttonView.getText().toString().toLowerCase(), isChecked);

        // Just quick way to check if nothing is checked. An optimization

        if (isChecked) {
            ++checkboxCounter;
        } else {
            --checkboxCounter;
        }
    }

    private void updateSpinner(boolean newUser) {

        // clear any existing user entries
        mUsers.clear();

        // query the database for all users' names
        Cursor cursor = mDatabaseHelper.getReadableDatabase().rawQuery(
                "SELECT name FROM users", null);

        if(cursor != null && cursor.moveToFirst()) {

            // iterate through all returned entries
            while(!cursor.isAfterLast()) {

                // add the user-name to the list of users
                String username = cursor.getString(0);
                mUsers.add(username);

                cursor.moveToNext();
            }

            cursor.close();

            // no existing users in the database
        } else {

            // create a toast indicating there are no existing users
            Toast.makeText(getActivity(), "No users yet. You should create one!",
                    Toast.LENGTH_SHORT).show();
            openCreateUserDialog();

        }

        // notify the adapter that the underlying view should be redrawn
        mAdapter.notifyDataSetChanged();

        if (newUser) {
            mUsernameSpinner.setSelection(mUsers.size());
        }
    }

    private void openCreateUserDialog() {
        FragmentUserCreation newFragment = FragmentUserCreation.newInstance();
        newFragment.setFragmentListener(this);
        newFragment.show(getActivity().getSupportFragmentManager(), "show");
    }

    @Override
    public void newUserCreated() {
        updateSpinner(true);
    }
}
