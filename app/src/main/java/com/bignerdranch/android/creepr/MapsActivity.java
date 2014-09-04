package com.bignerdranch.android.creepr;

import android.support.v4.app.Fragment;

public class MapsActivity extends SingleFragmentActivity {

    private static final String TAG = MapsActivity.class.getSimpleName();

    @Override
    protected Fragment createFragment() {
        return new MapsFragment();
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        MapsFragment fragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
//
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            Log.i(TAG, "Received a new search query: " + query);
//
//            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(FlickrFetchr.PREF_SEARCH_QUERY, query).commit();
//        }
//
//        fragment.updateMap();
//    }
}
