package com.example.shakeddesk.myapplication4;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.shakeddesk.myapplication4.useful.L;

public class OldLocationListenActivity extends AppCompatActivity implements LocationListener, View.OnClickListener {

    private TextView longTv,latTv;
    private Button startBtn, stopBtn;

    private boolean isListening;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_location_listen);
        initViews(); // THIS IS IMPORTANT ! without calling this method many exceptions will fly !

        startBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        this.isListening = false;

    }

    private void initViews() {
        longTv = (TextView) findViewById(R.id.oldLongTV);
        latTv = (TextView) findViewById(R.id.oldLatTV);

        startBtn = (Button) findViewById(R.id.oldStartListenBtn);
        stopBtn = (Button) findViewById(R.id.oldStopListenBtn);
    }




    private void startListening() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            L.log("Location update has been requested");
            updateListening(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void stopListening() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.removeUpdates(this);
            L.log("Location updates has been removed");
            updateListening(false);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void updateListening(boolean listening) {
        this.startBtn.setEnabled(!listening);
        this.stopBtn.setEnabled(listening);
        this.isListening = listening;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.oldStartListenBtn:
                startListening();
                break;
            case R.id.oldStopListenBtn:
                stopListening();
                break;
            default:
                L.log("Error! onClick called with unexpected view");
                break;
        }
    }
    /* LOCATION LISTENER */
    @Override
    public void onLocationChanged(Location location) {
        L.log();
        this.latTv.setText(location.getLatitude() + "");
        this.longTv.setText(location.getLongitude()+"");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle bundle) {
        L.log();
        L.log("Provider: " + provider);
        if (status == LocationProvider.AVAILABLE) {
            L.log("Status: AVAILABLE");
        } else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
            L.log("Status: TEMPORARILY_UNAVAILABLE");
        } else if (status == LocationProvider.OUT_OF_SERVICE) {
            L.log("Status: OUT_OF_SERVICE");
        }

        if (bundle != null) {
            try {
                int satelites = bundle.getInt("satelites");
                L.log("number of satelites: " + satelites);
            } catch (Exception e) {
                L.log("No field in bundle");
            }
        } else {
            L.log("Bundle was null");
        }
    }

    @Override
    public void onProviderEnabled(String s) {
        L.log();
    }

    @Override
    public void onProviderDisabled(String s) {
        L.log();
    }
    /* LOCATION LISTENER END */
}
