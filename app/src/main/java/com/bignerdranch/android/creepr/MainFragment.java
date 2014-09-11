package com.bignerdranch.android.creepr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.widget.LoginButton;

import java.util.Arrays;

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("user_hometown", "user_location", "user_friends", "friends_hometown", "friends_location"));

        return view;
    }

}