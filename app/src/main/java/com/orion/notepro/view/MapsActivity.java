package com.orion.notepro.view;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orion.notepro.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(this);
        mMap = googleMap;

        LatLng latlng = new LatLng(43.7615, -79.4110);
        mMap.addMarker(new MarkerOptions().position(latlng).title("Lambton College"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo( 10.0f ));
    }
}
