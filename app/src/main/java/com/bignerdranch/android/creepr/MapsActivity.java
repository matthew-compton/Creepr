package com.bignerdranch.android.creepr;

import android.support.v4.app.Fragment;

public class MapsActivity extends SingleFragmentActivity {

    private static final String TAG = MapsActivity.class.getSimpleName();

    @Override
    protected Fragment createFragment() {
        return new MapsFragment();
    }

}
