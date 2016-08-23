package com.jordanleex13.hangman;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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


public class FragmentWordSelection extends Fragment implements View.OnClickListener {

    public static final String TAG = FragmentWordSelection.class.getSimpleName();

    private TextView mTextView;
    private EditText mEditText;
    private Button mStartButton;

    private UserDataInputted mListener;

    private int mPlayerNum;

    public static FragmentWordSelection newInstance() {
        FragmentWordSelection fragment = new FragmentWordSelection();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_word_selection, container, false);

        Log.e(TAG, "onCreateVIEW");

        mTextView = (TextView) v.findViewById(R.id.fragment_word_selection_player);

        mEditText = (EditText) v.findViewById(R.id.fragment_word_selection_edittext);
        mEditText.setFilters(new InputFilter[] {
                new InputFilter() {
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
                }
        });

        mEditText.setText("test word");
        mStartButton = (Button) v.findViewById(R.id.fragment_word_selection_button_start);
        mStartButton.setOnClickListener(this);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPlayerNum = PrefUtils.getIntPreference(getActivity(), PrefUtils.CURRENT_TURN);
        mTextView.setText("User " + mPlayerNum + " please select a word");


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
                if (mListener.usernamesAreValid()) {
                    String word = mEditText.getText().toString();
                    if (word.isEmpty()) {
                        mEditText.setError("Please enter a word");
                    } else {
                        Log.e(TAG, "START game");
                        Intent twoPlayerIntent = new Intent(getActivity(), ActivityTwoPlayerGamePlay.class);
                        twoPlayerIntent.putExtra("word", word);
                        twoPlayerIntent.putExtra("playerNum", mPlayerNum);
                        getActivity().startActivity(twoPlayerIntent);
                    }
                }

                break;


        }
    }


    public interface UserDataInputted {
        boolean usernamesAreValid();
    }
    public void setUserDataInputtedListener(Fragment fragment) {
        mListener = (UserDataInputted) fragment;
    }

}
