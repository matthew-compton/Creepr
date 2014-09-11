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
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
    private Request.GraphUserListCallback mRequestGraphFriendsCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mStatusCallback = new SessionStatusCallback();
        mRequestGraphUserCallback = new RequestGraphUserCallback();
        mRequestGraphFriendsCallback = new RequestGraphFriendsCallback();

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
            Object hometownObject = user.getProperty("hometown");
            if (hometownObject == null) {
                return;
            }
            JSONObject jsonObject = new JSONObject(hometownObject.toString());
            hometownString = jsonObject.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GraphPlace locationObject = user.getLocation();
        String locationString = locationObject.getName();
        if (locationObject == null) {
            return;
        }

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

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(hometown);
        builder.include(location);
        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private void addFriend(GraphUser friend) {
        String title = friend.getName();
        String hometownString = "";
        try {
            Object hometownObject = friend.getProperty("hometown");
            if (hometownObject == null) {
                return;
            }
            JSONObject jsonObject = new JSONObject(hometownObject.toString());
            hometownString = jsonObject.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GraphPlace locationObject = friend.getLocation();
        String locationString = locationObject.getName();
        if (locationObject == null) {
            return;
        }

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
                Request request = Request.newMeRequest(session, mRequestGraphUserCallback);
                request.executeAsync();

                Request friendRequest = Request.newMyFriendsRequest(session, mRequestGraphFriendsCallback);
                Bundle params = new Bundle();
                params.putString("fields", "id,name,hometown,location");
                friendRequest.setParameters(params);
                friendRequest.executeAsync();
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

    private class RequestGraphFriendsCallback implements Request.GraphUserListCallback {
        @Override
        public void onCompleted(List<GraphUser> friends, Response response) {
            Log.i(TAG, response.toString());
            for (GraphUser friend : friends) {
                if (friend != null) {
                    addFriend(friend);
                }
            }
        }
    }

}