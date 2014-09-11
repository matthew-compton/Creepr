package com.bignerdranch.android.creepr;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private static final String TAG = CustomInfoWindowAdapter.class.getSimpleName();

    private View mInfoWindowView = null;
    private LayoutInflater mLayoutInflater = null;

    CustomInfoWindowAdapter(LayoutInflater layoutInflater) {
        this.mLayoutInflater = layoutInflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return (null);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getInfoContents(Marker marker) {
        if (mInfoWindowView == null) {
            mInfoWindowView = mLayoutInflater.inflate(R.layout.adapter_info_window, null);
        }

        TextView titleTextView = (TextView) mInfoWindowView.findViewById(R.id.adapter_info_window_title);
        titleTextView.setText(marker.getTitle());

        TextView snippetTextView = (TextView) mInfoWindowView.findViewById(R.id.adapter_info_window_snippet);
        snippetTextView.setText(marker.getSnippet());

        return mInfoWindowView;
    }

}