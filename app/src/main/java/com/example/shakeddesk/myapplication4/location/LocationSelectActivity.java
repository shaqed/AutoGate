package com.example.shakeddesk.myapplication4.location;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.shakeddesk.myapplication4.R;
import com.example.shakeddesk.myapplication4.useful.L;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class LocationSelectActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_select);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        L.log();
        googleMap.setOnMapClickListener(this);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            L.toast(this, "No permission to use your location");
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        L.log("Location: lat: " + latLng.latitude + " long: " + latLng.longitude);
        Intent resultIntent = new Intent();
        resultIntent.putExtra(LatLng.class.getSimpleName(), latLng);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
