package com.bignerdranch.android.creepr;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class MapsFragment extends Fragment {

    private static final String TAG = MapsFragment.class.getSimpleName();

    private GoogleMap mMap;
    private CustomInfoWindowAdapter mCustomInfoWindowAdapter;

    private Session.StatusCallback mStatusCallback;
    private Request.GraphUserCallback mRequestGraphUserCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mStatusCallback = new SessionStatusCallback();
        mRequestGraphUserCallback = new RequestGraphUserCallback();

        Session.openActiveSession(getActivity(), true, mStatusCallback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, parent, false);

        setUpMapIfNeeded();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                }
            }
        });


        mCustomInfoWindowAdapter = new CustomInfoWindowAdapter(getActivity().getLayoutInflater());
        mMap.setInfoWindowAdapter(mCustomInfoWindowAdapter);
    }

    private void addUser(GraphUser user) {
        String title = user.getName();
        String hometownString = "";
        try {
            JSONObject jsonObject = new JSONObject(user.getProperty("hometown").toString());
            hometownString = jsonObject.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String locationString = user.getLocation().getName();
        LatLng hometown = getLocationFromAddress(hometownString);
        LatLng location = getLocationFromAddress(locationString);

        MarkerOptions markerHometown = new MarkerOptions()
                .title(title)
                .snippet("Hometown:\n" + hometownString)
                .position(hometown);

        MarkerOptions markerLocation = new MarkerOptions()
                .title(title)
                .snippet("Current Location:\n" + locationString)
                .position(location);

        PolylineOptions line = new PolylineOptions().add(hometown, location);

        mMap.addMarker(markerHometown);
        mMap.addPolyline(line);
        mMap.addMarker(markerLocation);

        CameraUpdate center = CameraUpdateFactory.newLatLng(location);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(9);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }

    public LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(getActivity());
        List<Address> address = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return new LatLng(0, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address location = address.get(0);
        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
        return point;
    }

    private class SessionStatusCallback implements Session.StatusCallback {

        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if (session.isOpened()) {
                Request.newMeRequest(session, mRequestGraphUserCallback).executeAsync();
            }
        }
    }

    private class RequestGraphUserCallback implements Request.GraphUserCallback {
        @Override
        public void onCompleted(GraphUser user, Response response) {
            Log.i(TAG, response.toString());
            if (user != null) {
                addUser(user);
            }
        }
    }

}