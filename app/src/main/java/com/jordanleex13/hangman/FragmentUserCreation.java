package com.jordanleex13.hangman;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jordanleex13.hangman.Helpers.DatabaseHelper;


public class FragmentUserCreation extends DialogFragment implements View.OnClickListener {

    public static final String TAG = FragmentUserCreation.class.getSimpleName();

    private EditText mPlayerName;
    private EditText mPlayerAge;
    private Button mFinishButton;

    private OnUserCreatedListener mListener;

    private DatabaseHelper mDatabaseHelper;

    public static FragmentUserCreation newInstance() {

        FragmentUserCreation fragment = new FragmentUserCreation();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabaseHelper = new DatabaseHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_one_player_user_creation, container, false);

        getDialog().setTitle("User Creation");
        mPlayerName = (EditText) v.findViewById(R.id.fragment_user_creation_player_name);
        mPlayerAge = (EditText) v.findViewById(R.id.fragment_user_creation_player_age);

        mFinishButton = (Button) v.findViewById(R.id.fragment_user_creation_finish_button);
        mFinishButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onDestroy() {
        mDatabaseHelper.close();
        super.onDestroy();
    }

    private boolean validationCheck() {
        boolean readyToStart = true;

        String name = mPlayerName.getText().toString();
        if (name.isEmpty()) {
            mPlayerName.setError("This field cannot be blank");
            readyToStart = false;
        }
        String age = mPlayerAge.getText().toString();

        if (age.isEmpty()) {
            mPlayerAge.setError("This field cannot be blank");
            readyToStart = false;
        }


        Cursor cursor = mDatabaseHelper.getReadableDatabase().rawQuery(
                "SELECT name FROM users", null
        );
        // Could be null if first user created
        if (cursor != null && cursor.moveToFirst()) {

            do {
                String currName = cursor.getString(0);

                if (currName.equals(name)) {
                    Toast.makeText(getActivity(), "User with that name already exists", Toast.LENGTH_SHORT).show();
                    readyToStart = false;
                }

            } while (cursor.moveToNext());
            cursor.close();
        }



        return readyToStart;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_user_creation_finish_button:

                if (validationCheck()) {

                    insertToUserTable();

                    insertToUserStatsTable();

                    mDatabaseHelper.close();

                    if (mListener != null) {
                        mListener.newUserCreated();
                    } else {
                        Log.e(TAG, "listener was null");
                    }

                    dismiss();
                }
                break;

        }
    }

    private void insertToUserTable() {
        ContentValues args = new ContentValues();
        args.put("name", mPlayerName.getText().toString());
        args.put("age", mPlayerAge.getText().toString());

        mDatabaseHelper.getWritableDatabase().insert("users", null, args);
    }
    private void insertToUserStatsTable() {

        Cursor cursor = mDatabaseHelper.getReadableDatabase().rawQuery("SELECT MAX(_id) FROM users", null);
        cursor.moveToFirst();
        int newUserId = cursor.getInt(0);

        ContentValues args = new ContentValues();
        args.put("userId", newUserId);
        args.put("totalWins", 0);
        args.put("totalLosses", 0);
        args.put("animalsWins", 0);
        args.put("animalsLosses", 0);
        args.put("scienceWins", 0);
        args.put("scienceLosses", 0);
        args.put("sportsWins", 0);
        args.put("sportsLosses", 0);
        args.put("peopleWins", 0);
        args.put("peopleLosses", 0);
        args.put("landmarksWins", 0);
        args.put("landmarksLosses", 0);
        args.put("pokemonWins", 0);
        args.put("pokemonLosses", 0);

        mDatabaseHelper.getWritableDatabase().insert("userStats", null, args);

    }

    public interface OnUserCreatedListener {
        void newUserCreated();
    }

    public void setFragmentListener(Fragment fragment) {
        mListener = (OnUserCreatedListener) fragment;
    }
}
