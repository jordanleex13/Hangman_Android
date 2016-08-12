package com.jordanleex13.hangman;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentOnePlayerGamePlay#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentOnePlayerGamePlay extends Fragment implements View.OnClickListener{

    public static final String TAG = FragmentOnePlayerGamePlay.class.getSimpleName();

    private static final int GRID_LAYOUT_ID = 26;

    private int numOfWrongGuesses = 0;
    private int numOfFoundLetters = 0;
    private int numOfNonLetters = 0;

    private String mCategory;
    private String mDifficulty;
    private String mWord;
    private char[] mCharArray;  // INCLUDES SPACES
    private int mWordLength;    // INCLUDES SPACES

    private List<TextView> mListOfLetters = new ArrayList<>();  // INCLUDES SPACES

    private LinearLayout mLinearLayout;
    private GridLayout mGridLayout;
    private ImageView mHangman;
    private Button mConcedeButton;

    public static FragmentOnePlayerGamePlay newInstance(String category, String difficulty) {
        FragmentOnePlayerGamePlay fragment = new FragmentOnePlayerGamePlay();
        Bundle args = new Bundle();
        args.putString("category", category);
        args.putString("difficulty", difficulty);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCategory = getArguments().getString("category");
        mDifficulty = getArguments().getString("difficulty");
        Log.d(TAG, "Text file : " + mCategory + "_" + mDifficulty);
        setUpWord();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_one_player_game_play, container, false);

        mHangman = (ImageView) v.findViewById(R.id.fragment_one_player_game_play_hangman_image);
        mHangman.setImageBitmap(BitmapHelper.getBitmapFromMemCache("0"));

        Log.d(TAG, "Word length : " + String.valueOf(mWordLength));


        // TODO implement grid layout where each row is a new word maybe?
        mLinearLayout = (LinearLayout) v.findViewById(R.id.fragment_one_player_game_play_linear_layout);

        //Create four
        for(int j = 0; j < mWordLength; j++) {

            // Create TextView
            TextView underscore = new TextView(getActivity());
            underscore.setGravity(View.TEXT_ALIGNMENT_CENTER);
            underscore.setTextSize(getResources().getDimension(R.dimen.extra_small_text));
            underscore.setId(j);
            underscore.setPadding(8,0,8,0);

            mListOfLetters.add(underscore);

            Log.d(TAG, String.valueOf(mCharArray[j]));

            if (mCharArray[j] == ' ') {
                Log.e(TAG, "SPACE");
                underscore.setText("  ");
                ++numOfNonLetters;

            } else if (mCharArray[j] == '-') {
                Log.e(TAG, "HYPHEN");
                underscore.setText("-");
                ++numOfNonLetters;
            } else {
                underscore.setText("__");
            }

            mLinearLayout.addView(underscore);
        }



        mGridLayout = (GridLayout) v.findViewById(R.id.fragment_one_player_game_play_alphabet);
        setGridListeners();

        mConcedeButton = (Button) v.findViewById(R.id.fragment_one_player_game_play_give_up);
        mConcedeButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_one_player_game_play_give_up:
//                Toast.makeText(getActivity(), "Secret word was '" + mWord + "'", Toast.LENGTH_LONG).show();
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

    /*
     * Utility functions
     */
    private void returnToPrevious() {
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }



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

    @SuppressWarnings({"All"})
    private void setGridListeners() {

        final int size = mGridLayout.getChildCount();
        Log.e(TAG, "size of grid layout : " + String.valueOf(size));

        for (int i = 0; i < size; ++i ) {
            TextView temp = (TextView) mGridLayout.getChildAt(i);
            temp.setId(GRID_LAYOUT_ID);
            temp.setOnClickListener(this);
            Log.e(TAG, temp.getText().toString());
        }
    }



    private void checkGuess(View v) {
        TextView view = (TextView) v;
        Log.d(TAG, view.getText().toString());
        view.setOnClickListener(null);

        // Logic
        int[] binary = new int[mWordLength];
        boolean found = false;
        for (int i = 0; i < mWordLength; i++) {
            char c = view.getText().toString().toCharArray()[0];

            if (c == mCharArray[i]) {
                // Interesting, by default it's already set as 0
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
                    Log.e(TAG, temp.getText().toString());
                    Log.e(TAG, "FOUND at : " + String.valueOf(i));
                    ++numOfFoundLetters;

                    if (numOfFoundLetters == (mWordLength - numOfNonLetters)) {
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
                    showAlertDialog("CPU Wins!");
                }

                Bitmap bm = BitmapHelper.getBitmapFromMemCache(String.valueOf(numOfWrongGuesses));
                if (bm != null) {
                    mHangman.setImageBitmap(bm);
                } else {
                    Log.e(TAG, "Error loading image from cache");
                }
            }
        }

    }

    /**
     * Fills in the rest of the letters red
     */
    private void fillInRestOfLetters() {
        for (int i = 0; i < mListOfLetters.size(); ++i) {
            TextView temp = mListOfLetters.get(i);

            String letter = temp.getText().toString();
            if (letter.equals("_") || letter.equals(" ")) {

                temp.setText(String.valueOf(mCharArray[i]));
                temp.setTextColor(getResources().getColor(R.color.red));

            }
        }
    }

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

}
