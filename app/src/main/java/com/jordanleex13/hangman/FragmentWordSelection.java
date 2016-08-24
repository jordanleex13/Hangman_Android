package com.jordanleex13.hangman;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.jordanleex13.hangman.Helpers.FileHelper;
import com.jordanleex13.hangman.Helpers.PrefUtils;
import com.jordanleex13.hangman.Models.CategoryData;

import java.util.Random;

/**
 * Fragment where a user selects a word for the other user.
 * Can manually enter a word or select a category for which a random word in that category will be chosen
 */
public class FragmentWordSelection extends Fragment implements View.OnClickListener, RVAdapterCategories.ViewHolder.OnCategoryClickedListener {

    public static final String TAG = FragmentWordSelection.class.getSimpleName();

    private TextView mTextView;
    private EditText mEditText;
    private Button mStartButton;
    private RecyclerView mRecyclerView;
    private RVAdapterCategories mRVAdapter;

    private UserDataInputted mListener;

    private int mPlayerNum;

    private String mWord;
    private final int MAX_WORD_LENGTH = 30;


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

        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_word_selection_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRVAdapter = new RVAdapterCategories();
        mRVAdapter.setOnCategoryClickedListener(this);

        mRecyclerView.setAdapter(mRVAdapter);


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
        mTextView.setText(Html.fromHtml("<b><i>User " + oppositeNum + "</b></i> please type a word <b>OR</b> select a category below to choose a random word"));
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
                    mWord = mEditText.getText().toString();

                    if (mWord.isEmpty()) {
                        mEditText.setError("Please enter a word");

                    } else {
                        Log.e(TAG, "Fields valid. Starting game");
                        Intent twoPlayerIntent = new Intent(getActivity(), ActivityTwoPlayerGamePlay.class);
                        twoPlayerIntent.putExtra("word", mWord);
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


    /**
     * Selects a random word (with a random difficulty) from the clicked category
     * @param itemView
     */
    @Override
    public void onCategoryClicked(View itemView) {
        int position = mRecyclerView.getChildLayoutPosition(itemView);

        String mCategory = CategoryData.CATEGORY_LIST[position].toLowerCase();

        String mDifficulty = "medium";

        // select a random difficulty level
        Random random = new Random();
        switch(random.nextInt(3)) {
            case 0:
                mDifficulty = "easy";
                break;
            case 1:
                mDifficulty = "medium";
                break;
            case 2:
                mDifficulty = "hard";
                break;
        }

        // Select a random word from the assigned category and difficulty
        int resId = FileHelper.getStringIdentifier(getActivity(), mCategory + "_" + mDifficulty, "raw");
        mWord = FileHelper.selectRandomWord(getActivity(), resId);

        // set the edit text
        mEditText.setText(mWord);
    }


    // Interface to communicate with other fragment in view pager
    public interface UserDataInputted {
        boolean usernamesAreValid();
    }

    public void setUserDataInputtedListener(Fragment fragment) {
        mListener = (UserDataInputted) fragment;
    }

}
