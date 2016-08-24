package com.jordanleex13.hangman;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FragmentTitle extends Fragment implements View.OnClickListener {

    public static final String TAG = FragmentTitle.class.getSimpleName();

    // UI elements
    private Button onePlayerButton;
    private Button twoPlayersButton;

    public static FragmentTitle newInstance() {
        return new FragmentTitle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_title, container, false);

        onePlayerButton = (Button) v.findViewById(R.id.fragment_title_button_one_player);
        twoPlayersButton = (Button) v.findViewById(R.id.fragment_title_button_two_players);

        onePlayerButton.setOnClickListener(this);
        twoPlayersButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.fragment_title_button_one_player:

                Intent singlePlayerIntent = new Intent(getActivity(), ActivityOnePlayer.class);
                getActivity().startActivity(singlePlayerIntent);

                break;

            case R.id.fragment_title_button_two_players:

                Intent twoPlayerIntent = new Intent(getActivity(), ActivityTwoPlayer.class);
                getActivity().startActivity(twoPlayerIntent);

                break;

            default:
                Log.e(TAG, "Unknown click registered " + v.getId());
                break;
        }
    }
}
