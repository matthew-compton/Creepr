package com.bignerdranch.android.creepr;

import android.app.SearchManager;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;

public class MapsActivity extends SingleFragmentActivity {

    private static final String TAG = MapsActivity.class.getSimpleName();

    private static final String PREF_SEARCH_QUERY = "searchQuery";

    @Override
    protected Fragment createFragment() {
        return new MapsFragment();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i(TAG, "Received a new search query: " + query);

            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(PREF_SEARCH_QUERY, query).commit();

            // TODO - use the query to search the data somehow
//            MapsFragment fragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
//            fragment.updateMap();
        }
    }

}
