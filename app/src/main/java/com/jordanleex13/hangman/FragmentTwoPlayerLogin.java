package com.jordanleex13.hangman;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.jordanleex13.hangman.Helpers.FragmentHelper;
import com.jordanleex13.hangman.Helpers.PrefUtils;
import com.jordanleex13.hangman.Models.CategoryData;

/**
 * Fragment where the two players input their names
 * TODO display user win rates against each other and a clear button to erase "current session" history
 */
public class FragmentTwoPlayerLogin extends Fragment implements FragmentWordSelection.UserDataInputted {

    public static final String TAG = FragmentTwoPlayerLogin.class.getSimpleName();

    private EditText user1;
    private EditText user2;

    public static FragmentTwoPlayerLogin newInstance() {
        FragmentTwoPlayerLogin fragment = new FragmentTwoPlayerLogin();
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
        View v = inflater.inflate(R.layout.fragment_two_player_login, container, false);

        user1 = (EditText) v.findViewById(R.id.fragment_two_player_login_user1name);
        user2 = (EditText) v.findViewById(R.id.fragment_two_player_login_user2name);

        user1.setText(PrefUtils.getStringPreference(getActivity(), PrefUtils.PLAYER_ONE_NAME));
        user2.setText(PrefUtils.getStringPreference(getActivity(), PrefUtils.PLAYER_TWO_NAME));

        TextView user1Wins = (TextView) v.findViewById(R.id.fragment_two_player_login_user1_wins);
        TextView user2Wins = (TextView) v.findViewById(R.id.fragment_two_player_login_user2_wins);

        TextView user1Losses = (TextView) v.findViewById(R.id.fragment_two_player_login_user1_losses);
        TextView user2Losses = (TextView) v.findViewById(R.id.fragment_two_player_login_user2_losses);

        TextView user1Rate = (TextView) v.findViewById(R.id.fragment_two_player_login_user1_winrate);
        TextView user2Rate = (TextView) v.findViewById(R.id.fragment_two_player_login_user2_winrate);

        int p1wins = PrefUtils.getIntPreference(getActivity(), PrefUtils.PLAYER_ONE_WINS);
        int p2wins = PrefUtils.getIntPreference(getActivity(), PrefUtils.PLAYER_TWO_WINS);
        user1Wins.setText("Wins : " + p1wins);
        user2Wins.setText("Wins : " + p2wins);

        int p1losses = PrefUtils.getIntPreference(getActivity(), PrefUtils.PLAYER_ONE_LOSSES);
        int p2losses = PrefUtils.getIntPreference(getActivity(), PrefUtils.PLAYER_TWO_LOSSES);
        user1Losses.setText("Losses : " + p1losses);
        user2Losses.setText("Losses : " + p2losses);

        user1Rate.setText("Win Rate : " + CategoryData.staticCalculateWinRate(p1wins, p1losses));
        user2Rate.setText("Win Rate : " + CategoryData.staticCalculateWinRate(p2wins, p2losses));

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentHelper.setUpActionBar(getActivity(), true, "Two Player");
    }


    private boolean validationCheck() {

        boolean readyToStart = true;

        String user1name = user1.getText().toString();
        if (user1name.isEmpty()) {
            user1.setError("This field cannot be blank");
            readyToStart = false;
        }
        String user2name = user2.getText().toString();

        if (user2name.isEmpty()) {
            user2.setError("This field cannot be blank");
            readyToStart = false;
        } else if (user1name.equals(user2name)) {
            user2.setError("Cannot have the same name");
            readyToStart = false;
        }

        PrefUtils.setStringPreference(getActivity(), PrefUtils.PLAYER_ONE_NAME, user1name);
        PrefUtils.setStringPreference(getActivity(), PrefUtils.PLAYER_TWO_NAME, user2name);

        return readyToStart;
    }


    /**
     * Callback returned to FragmentWordSelection. Switches tabs if not everything is correctly filled out
     * @return  True if this fragment has all the info filled out correctly, else false
     */
    @Override
    public boolean usernamesAreValid() {
        if (validationCheck()) {
            return true;
        } else {
            ActivityTwoPlayer activity = (ActivityTwoPlayer) getActivity();
            activity.switchViewPagerTab(0);
            return false;
        }
    }
}
