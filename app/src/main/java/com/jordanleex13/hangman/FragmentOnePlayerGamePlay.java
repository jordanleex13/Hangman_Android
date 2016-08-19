package com.jordanleex13.hangman;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jordanleex13.hangman.Helpers.BitmapHelper;
import com.jordanleex13.hangman.Helpers.FileHelper;
import com.jordanleex13.hangman.Helpers.FragmentHelper;
import com.jordanleex13.hangman.Helpers.RunnableSaveData;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment where user tries to guess the hidden word. User has 8 tries to guess correctly. UI elements
 * are updated accordingly after a user guess
 */
public class FragmentOnePlayerGamePlay extends Fragment implements View.OnClickListener{

    public static final String TAG = FragmentOnePlayerGamePlay.class.getSimpleName();

    // UI
    private ImageView mHangman;
    private LinearLayout mLinearRow1;
    private LinearLayout mLinearRow2;
    private GridLayout mAlphabetGrid;
    private Button mConcedeButton;

    private List<TextView> mListOfLetters = new ArrayList<>();  // INCLUDES SPACES

    // From arguments
    private String mCategory;
    private String mDifficulty;
    private int mUserId;

    // Word variables
    private String mWord;       // Taken straight from text file as is
    private char[] mCharArray;  // INCLUDES SPACES  IS UPPER CASE
    private int mWordLength;    // INCLUDES SPACES

    private int numOfWrongGuesses = 0;
    private int numOfFoundLetters = 0;
    private int numOfNonLetters = 0;

    private final int GRID_LAYOUT_ID = 26;
    private final String UNDERSCORE = "__";
    private final String SPACE = " ";


    public static FragmentOnePlayerGamePlay newInstance(String category, String difficulty, int id) {
        FragmentOnePlayerGamePlay fragment = new FragmentOnePlayerGamePlay();
        Bundle args = new Bundle();
        args.putString("category", category);
        args.putString("difficulty", difficulty);
        args.putInt("userId", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCategory = getArguments().getString("category");
        mDifficulty = getArguments().getString("difficulty");
        mUserId = getArguments().getInt("userId");

        Log.d(TAG, "Text file : " + mCategory + "_" + mDifficulty);

        //FYI both arguments are lower case
        setUpWord();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_one_player_game_play, container, false);

        // Set initial image
        mHangman = (ImageView) v.findViewById(R.id.fragment_one_player_game_play_hangman_image);
        setUpHangmanBitmap();

        mLinearRow1 = (LinearLayout) v.findViewById(R.id.fragment_one_player_game_play_linear_layout);
        mLinearRow2 = (LinearLayout) v.findViewById(R.id.fragment_one_player_game_play_linear_layout_2);
        setUpWordUI();

        // Set up alphabet and listeners
        mAlphabetGrid = (GridLayout) v.findViewById(R.id.fragment_one_player_game_play_alphabet);
        setGridListeners();

        // Set up "Give Up" button
        mConcedeButton = (Button) v.findViewById(R.id.fragment_one_player_game_play_give_up);
        mConcedeButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentHelper.setUpActionBar(getActivity(), false, "Gameplay");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fragment_one_player_game_play_give_up:
                // Counts as a loss
                saveResults(false);
                returnToPrevious();
                break;

            case GRID_LAYOUT_ID:
                checkGuess(v);
                break;

            default:
                Log.e(TAG, "Unknown click registered with ID : " + String.valueOf(v.getId()));
                break;
        }
    }




    private void returnToPrevious() {
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }


    /**
     * Method to choose a random word from the specified category and difficulty level
     * Creates a character array of that string
     */
    private void setUpWord() {
        int resId = FileHelper.getStringIdentifier(getActivity(), mCategory + "_" + mDifficulty, "raw");
        mWord = FileHelper.selectRandomWord(getActivity(), resId);

        if (mWord == null) {
            Log.e(TAG, "DID NOT SELECT A WORD. ERROR!");
            returnToPrevious();
        } else {
            mCharArray = mWord.toUpperCase().toCharArray();
            Log.i(TAG, "The secret word is " + mWord);
            mWordLength = mWord.length();
        }
    }


    /**
     * Method to dynamically set up the word UI
     */
    private void setUpWordUI() {

        int whichLayoutToPick = 1;
        Log.d(TAG, "Word length : " + mWordLength);

        //Create a TextView for each letter
        for(int j = 0; j < mWordLength; j++) {

            // Create TextView
            TextView underscore = new TextView(getActivity());
            underscore.setGravity(View.TEXT_ALIGNMENT_CENTER);
            underscore.setTextSize(getResources().getDimension(R.dimen.extra_small_text));
            underscore.setId(j);
            underscore.setPadding(8, 0, 8, 0);

            // Add to list of TextViews to be accessed later
            mListOfLetters.add(underscore);

            if (BuildConfig.DEBUG) Log.d(TAG, String.valueOf(mCharArray[j]));

            char currentChar = mCharArray[j];
            if (currentChar >= 'A' && currentChar <= 'Z') {
                underscore.setText(UNDERSCORE);

            } else if (mCharArray[j] == ' ') {
                Log.e(TAG, "SPACE");
                underscore.setText(SPACE);
                ++numOfNonLetters;

                // after the first space, automatically default to the second row
                whichLayoutToPick = 2;

            } else {
                underscore.setText(Character.toString(currentChar));
                ++numOfNonLetters;
            }

            // Determine which row to set the letter in
            if (whichLayoutToPick == 1) {
                mLinearRow1.addView(underscore);

            } else if (whichLayoutToPick == 2) {
                mLinearRow2.addView(underscore);
            }
        }
    }


    /**
     * Method to set up correct drawable, whether from the bitmap cache or just from the drawable folder
     * Drawable number corresponds with the numOfWrongGuesses
     */
    private void setUpHangmanBitmap() {

        Bitmap bm = BitmapHelper.getBitmapFromMemCache(String.valueOf(numOfWrongGuesses));
        if (bm != null) {
            mHangman.setImageBitmap(bm);
        } else {
            Log.e(TAG, "Error loading image from cache. Setting default drawable");
            int resId = FileHelper.getStringIdentifier(getActivity(), "stage" + numOfWrongGuesses, "drawable");
            mHangman.setImageResource(resId);
        }
    }


    @SuppressWarnings({"All"})
    /**
     * Sets up OnClickListeners for each individual letter in the alphabet grid
     */
    private void setGridListeners() {

        // Should be 26
        final int size = mAlphabetGrid.getChildCount();

        Assert.assertTrue(size == 26);

        for (int i = 0; i < size; ++i ) {
            TextView temp = (TextView) mAlphabetGrid.getChildAt(i);
            temp.setId(GRID_LAYOUT_ID);
            temp.setOnClickListener(this);
        }
    }


    /**
     * Method that checks to see if the guessed letter is in the word, then updates the UI accordingly
     * @param v     The TextView in the alphabet grid that was clicked
     */
    private void checkGuess(View v) {
        TextView view = (TextView) v;
        Log.d(TAG, view.getText().toString());

        // Disable the click listener since should not be able to click a letter more than once
        view.setOnClickListener(null);

        // Logic
        int[] binary = new int[mWordLength];
        boolean found = false;
        for (int i = 0; i < mWordLength; i++) {
            char c = view.getText().toString().toCharArray()[0];

            if (c == mCharArray[i]) {
                // by default it's already set as 0
                binary[i] = 1;
                found = true;
            }
        }

        // UI
        if (found) {
            view.setTextColor(getResources().getColor(R.color.green));

            for (int i = 0; i < mWordLength; i++) {

                if (binary[i] == 1) {
                    TextView temp = mListOfLetters.get(i);
                    temp.setText(String.valueOf(mCharArray[i]));

                    Log.e(TAG, "FOUND letter " + view.getText().toString() + " at : " + String.valueOf(i));
                    ++numOfFoundLetters;

                    if (numOfFoundLetters == (mWordLength - numOfNonLetters)) {
                        saveResults(true);
                        showAlertDialog("You win!");
                    }
                }
            }

        } else {
            ++numOfWrongGuesses;
            view.setTextColor(getResources().getColor(R.color.red));


            if (numOfWrongGuesses > 8) {
                Log.e(TAG, "ERROR. Should have exited already");
            } else {

                if (numOfWrongGuesses == 8) {
                    fillInRestOfLetters();
                    saveResults(false);
                    showAlertDialog("CPU Wins!");
                }

                setUpHangmanBitmap();
            }
        }

    }


    /**
     * Fills in the rest of the letters red; used when the player loses
     */
    private void fillInRestOfLetters() {
        for (int i = 0; i < mListOfLetters.size(); ++i) {
            TextView temp = mListOfLetters.get(i);

            String letter = temp.getText().toString();
            if (letter.equals(UNDERSCORE) || letter.equals(SPACE)) {

                temp.setText(String.valueOf(mCharArray[i]));
                temp.setTextColor(getResources().getColor(R.color.red));

            }
        }
    }


    /**
     * Displays an alert dialog that has two options: try again or search
     * @param title     title of the alert dialog
     */
    private void showAlertDialog(String title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage("You got " + numOfFoundLetters + "/" + (mWordLength - numOfNonLetters) + " letters")
                .setCancelable(false)
                .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        returnToPrevious();
                    }
                })
                .setNegativeButton("Search " + mWord, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.parse("http://www.google.com/#q="+ mWord);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        returnToPrevious();

                    }
                });

        AlertDialog dialog = builder.create();

        // Set position of dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        wmlp.y = 100;   //y position

        dialog.show();
    }


    /**
     * Spawns a new thread to save the results of the game
     * @param win       true if user won, false if user lost
     */
    private void saveResults(boolean win) {
        new Thread(new RunnableSaveData(getActivity(), win, mCategory, mUserId)).start();
    }

}
