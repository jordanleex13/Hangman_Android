package com.jordanleex13.hangman;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FragmentTwoPlayerLogin extends Fragment {

    public static final String TAG = FragmentTwoPlayerLogin.class.getSimpleName();

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

        return v;
    }

}
