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
import com.orion.notepro.controller.DatabaseHelper;
import com.orion.notepro.model.Note;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<Note> notes;
    private LatLng noteEditLatLong;
    private int auxNote = 0;
    private int auxNotes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        DatabaseHelper dao = new DatabaseHelper(this);
        notes = dao.selectAllNotes();

        Bundle bundle = getIntent().getParcelableExtra("bundle");
        if (bundle != null) {
            noteEditLatLong = bundle.getParcelable("noteEditLatLong");
        }

        auxNote = getIntent().getIntExtra("eachNote",0);
        auxNotes = getIntent().getIntExtra("allNotes",0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(this);
        mMap = googleMap;

        if (auxNote > 0 && noteEditLatLong != null) {
            mMap.addMarker(new MarkerOptions().position(noteEditLatLong));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(noteEditLatLong));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
        } else if (auxNotes > 0 && notes != null) {
            for (Note note : notes) {
                mMap.addMarker(new MarkerOptions().position(note.getLatLng()));
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(43.7615, -79.4110)));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
        } else {
            LatLng latlng = new LatLng(43.7615, -79.4110);
            mMap.addMarker(new MarkerOptions().position(latlng));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
        }
    }
}
