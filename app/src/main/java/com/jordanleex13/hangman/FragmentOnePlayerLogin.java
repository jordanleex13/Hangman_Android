package com.jordanleex13.hangman;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.jordanleex13.hangman.Models.CategoryData;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * Fragment login where a user, difficulty, and a category(s) are selected.
 */
public class FragmentOnePlayerLogin extends Fragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, FragmentUserCreation.OnUserCreatedListener {

    public static final String TAG = FragmentOnePlayerLogin.class.getSimpleName();

    // UI elements
    private Spinner mUsernameSpinner;
    private CheckBox animals, science, sports, people, landmarks, pokemon, countries;
    private RadioGroup cpuDifficulty;
    private RadioButton selectedCpuDifficulty;      // set to medium by default
    private Button startButton;
    private Toast mToast;

    private DatabaseHelper mDatabaseHelper;
    private ArrayAdapter mSpinnerAdapter;
    private HashMap<String, Boolean> checkedHashMap;
    private int checkboxCounter;

    // data
    private String selectedCategory;
    private String selectedDifficulty;
    private String mCurrName;

    public static FragmentOnePlayerLogin newInstance() {
        return new FragmentOnePlayerLogin();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mDatabaseHelper = new DatabaseHelper(getActivity());
        checkedHashMap = new HashMap<>(CategoryData.NUM_OF_CATEGORIES);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Reset checkbox counter
        checkboxCounter = 0;

        // Have one toast for the entire fragment
        mToast = new Toast(getActivity());

        View v = inflater.inflate(R.layout.fragment_one_player_login, container, false);

        cpuDifficulty = (RadioGroup) v.findViewById(R.id.fragment_one_player_login_cpu_difficulty);
        startButton = (Button) v.findViewById(R.id.fragment_one_player_login_button_start);

        mUsernameSpinner = (Spinner) v.findViewById(R.id.fragment_one_player_login_spinner_username);
        // create and link the adapter to the spinner object
        mSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, ActivityOnePlayer.mUsers);
        mUsernameSpinner.setAdapter(mSpinnerAdapter);

        // Init UI checkboxes
        animals = (CheckBox) v.findViewById(R.id.fragment_one_player_login_category_animals);
        science = (CheckBox) v.findViewById(R.id.fragment_one_player_login_category_science);
        sports = (CheckBox) v.findViewById(R.id.fragment_one_player_login_category_sports);
        people = (CheckBox) v.findViewById(R.id.fragment_one_player_login_category_people);
        landmarks = (CheckBox) v.findViewById(R.id.fragment_one_player_login_category_landmarks);
        pokemon = (CheckBox) v.findViewById(R.id.fragment_one_player_login_category_pokemon);
        countries = (CheckBox) v.findViewById(R.id.fragment_one_player_login_category_countries);

        // Set listeners
        animals.setOnCheckedChangeListener(this);
        science.setOnCheckedChangeListener(this);
        sports.setOnCheckedChangeListener(this);
        people.setOnCheckedChangeListener(this);
        landmarks.setOnCheckedChangeListener(this);
        pokemon.setOnCheckedChangeListener(this);
        countries.setOnCheckedChangeListener(this);

        startButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentHelper.setUpActionBar(getActivity(), true, "One Player");

    }

    @Override
    public void onResume() {
        super.onResume();

        updateSpinner(false);

        Log.d(TAG, "Number of checked boxes : " + String.valueOf(checkboxCounter));

        checkedHashMap.put("animals", animals.isChecked());
        checkedHashMap.put("science", science.isChecked());
        checkedHashMap.put("sports", sports.isChecked());
        checkedHashMap.put("people", people.isChecked());
        checkedHashMap.put("landmarks", landmarks.isChecked());
        checkedHashMap.put("pokemon", pokemon.isChecked());
        checkedHashMap.put("countries", countries.isChecked());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseHelper.close();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.oneplayer_login_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:

                Log.d(TAG, "Home button handled by activity");

                break;

            case R.id.menu_create_user:

                openCreateUserDialog();

                break;

            case R.id.menu_statistics:

                if (ActivityOnePlayer.mUsers.isEmpty()) {
                    showToast("Please create a user");

                } else if (checkName()) {

                    Fragment newFragment = FragmentUserStats.newInstance(mCurrName);
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

                    int mUserId = mDatabaseHelper.getUserIdFromName(mCurrName);

                    Assert.assertTrue(selectedCategory != null && selectedDifficulty != null && mUserId != 0);

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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        checkedHashMap.put(buttonView.getText().toString().toLowerCase(), isChecked);

        // Just quick way to check if nothing is checked. An optimization
        if (isChecked) {
            ++checkboxCounter;
        } else {
            --checkboxCounter;
        }
    }



    /**
     * Method to check that all fields are filled out (name, difficulty, category)
     * @return  True if everything is valid, false otherwise
     */
    private boolean validationCheck() {
        boolean readyToStart;

        if (!ActivityOnePlayer.mUsers.isEmpty()) {
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
            showToast("Select at least one category");
            readyToStart = false;
        } else {
            Log.d(TAG, String.valueOf(checkboxCounter) + " categories picked");
            selectedCategory = selectRandomCategory();
        }
        return readyToStart;
    }


    /**
     * Ensures there is a name currently displayed in the spinner. Also initializes the userId corresponding
     * to that name
     * @return      True if there is a name, false otherwise
     */
    private boolean checkName() {
        String name = mUsernameSpinner.getSelectedItem().toString();

        if (name.isEmpty()) {
            return false;
        } else {
            mCurrName = name;
            return true;

        }
    }


    /**
     * Method that parses through the checkbox hashmap and selects a random category
     * @return  String of the category
     */
    private String selectRandomCategory() {
        // Parse through all checkboxes
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

        Log.d(TAG, "Number of checked boxes : "+ isCheckedAl.size());

        // Select a random number from [0, size) and get value from that index
        Random rand = new Random();
        int n = rand.nextInt(isCheckedAl.size());
        return isCheckedAl.get(n);
    }


    /**
     * Utility method to ensure multiple clicks of a toast don't stack
     * @param st        the message to toast
     */
    private void showToast (String st) {

        try{
            if (mToast.getView().isShown()) {
                mToast.setText(st);
            }
        } catch (Exception e) {         // invisible if exception
            Log.e(TAG, "Toast invisible. Create a new one");
            mToast = Toast.makeText(getActivity(), st, Toast.LENGTH_SHORT);
        }
        mToast.show();  //finally display it
    }


    /**
     * Method to update the spinner by going through the database and creating a new arraylist of users
     * @param newUser       true if a new user was created, else false
     */
    private void updateSpinner(boolean newUser) {

        // clear any existing user entries
        ActivityOnePlayer.mUsers.clear();

        // query the database for all users' names
        Cursor cursor = mDatabaseHelper.getReadableDatabase().rawQuery(
                "SELECT name FROM users", null);

        if(cursor != null && cursor.moveToFirst()) {

            // iterate through all returned entries
            while(!cursor.isAfterLast()) {

                // add the user-name to the list of users
                String username = cursor.getString(0);
                Log.e(TAG, username);
                ActivityOnePlayer.mUsers.add(username);

                cursor.moveToNext();
            }

            cursor.close();

        } else {

            // create a toast indicating there are no existing users
            showToast("No users yet. You should create one!");
            openCreateUserDialog();

        }

        // notify the adapter that the underlying view should be redrawn
        mSpinnerAdapter.notifyDataSetChanged();

        Log.e(TAG, "Size of users : " + ActivityOnePlayer.mUsers.size());

        // set the selection to the newly created user
        if (newUser) {
            mUsernameSpinner.setSelection(ActivityOnePlayer.mUsers.size());
        }
    }


    /**
     * Opens the user creation fragment
     */
    private void openCreateUserDialog() {
        FragmentUserCreation newFragment = FragmentUserCreation.newInstance();
        newFragment.setFragmentListener(this);
        newFragment.show(getActivity().getSupportFragmentManager(), "show");
    }


    /**
     * Callback from FragmentUserCreation that signals a new user was created. Update the spinner
     */
    @Override
    public void newUserCreated() {
        updateSpinner(true);
    }
}
