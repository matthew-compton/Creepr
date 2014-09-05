package com.bignerdranch.android.creepr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment {

    private static final String TAG = MapsFragment.class.getSimpleName();

    private GoogleMap mMap;

    private Session.StatusCallback statusCallback;
    private Request.GraphUserCallback requestGraphUserCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        statusCallback = new SessionStatusCallback();
        requestGraphUserCallback = new RequestGraphUserCallback();

        Session.openActiveSession(getActivity(), true, statusCallback);
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

        mMap.addMarker(new MarkerOptions()
                        .title("Marker")
                        .snippet("Information about this user.")
                        .position(new LatLng(0, 0))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
        );

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                }
            }
        });

    }

    private class SessionStatusCallback implements Session.StatusCallback {

        @Override
        public void call(Session session, SessionState state, Exception exception) {

            if (session.isOpened()) {
                Request.newMeRequest(session, requestGraphUserCallback).executeAsync();
            }

        }
    }

    private class RequestGraphUserCallback implements Request.GraphUserCallback {

        @Override
        public void onCompleted(GraphUser user, Response response) {
            if (user != null) {
                Toast.makeText(getActivity().getApplicationContext(), user.getName(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}