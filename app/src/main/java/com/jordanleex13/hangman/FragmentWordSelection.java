package com.jordanleex13.hangman;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jordanleex13.hangman.Helpers.PrefUtils;

/**
 * Fragment where a user enters a word for another user
 */
public class FragmentWordSelection extends Fragment implements View.OnClickListener {

    public static final String TAG = FragmentWordSelection.class.getSimpleName();

    private TextView mTextView;
    private EditText mEditText;
    private Button mStartButton;

    private UserDataInputted mListener;

    private int mPlayerNum;

    private final int MAX_WORD_LENGTH = 12;


    public static FragmentWordSelection newInstance() {
        FragmentWordSelection fragment = new FragmentWordSelection();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_word_selection, container, false);

        mTextView = (TextView) v.findViewById(R.id.fragment_word_selection_player);

        mEditText = (EditText) v.findViewById(R.id.fragment_word_selection_edittext);
        mEditText.setFilters(createInputFilters());

        mStartButton = (Button) v.findViewById(R.id.fragment_word_selection_button_start);
        mStartButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set who should pick the word. Opposite of who is playing
        mPlayerNum = PrefUtils.getIntPreference(getActivity(), PrefUtils.CURRENT_TURN);
        int oppositeNum;
        if (mPlayerNum == 1) {
            oppositeNum = 2;
        } else {
            oppositeNum = 1;
        }
        mTextView.setText(Html.fromHtml("<b><i>User " + oppositeNum + "</b></i> please select a word"));
        mEditText.setText(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "on Pause");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_word_selection_button_start:

                // Check FragmentTwoPlayerLogin if usernames are valid
                if (mListener.usernamesAreValid()) {

                    // Check if word is filled
                    String word = mEditText.getText().toString();

                    if (word.isEmpty()) {
                        mEditText.setError("Please enter a word");

                    } else {
                        Log.e(TAG, "Fields valid. Starting game");
                        Intent twoPlayerIntent = new Intent(getActivity(), ActivityTwoPlayerGamePlay.class);
                        twoPlayerIntent.putExtra("word", word);
                        twoPlayerIntent.putExtra("playerNum", mPlayerNum);
                        getActivity().startActivity(twoPlayerIntent);
                    }
                }

                break;


        }
    }


    /**
     * Private method to create input filters for edit text
     * @return      input filter array
     */
    private InputFilter[] createInputFilters() {

        InputFilter[] filterArray = new InputFilter[2];

        filterArray[0] = new InputFilter.LengthFilter(MAX_WORD_LENGTH);
        filterArray[1] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence src, int start, int end, Spanned dest, int dstart, int dend) {
                if(src.equals("")){ // for backspace
                    return src;
                }
                if(src.toString().matches("[a-zA-Z ]+")){
                    return src;
                }
                return "";
            }
        };

        return filterArray;
    }


    // Interface to communicate with other fragment in view pager
    public interface UserDataInputted {
        boolean usernamesAreValid();
    }

    public void setUserDataInputtedListener(Fragment fragment) {
        mListener = (UserDataInputted) fragment;
    }

}
