package com.example.shakeddesk.myapplication4.location;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.example.shakeddesk.myapplication4.useful.L;
import com.example.shakeddesk.myapplication4.useful.SharedPreferenceHandler;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;


public class GeofenceIntentService extends IntentService {
    public GeofenceIntentService() {
        super("GeoFenceIS");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            L.log("Error: " + GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode()));
            return;
        }

        int transitionType = geofencingEvent.getGeofenceTransition();
        SharedPreferenceHandler handler = new SharedPreferenceHandler(this);

        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
            String phoneNumber = handler.getPhoneNumber();
            L.log("thread: " + Thread.currentThread().getName() + " ENTER DETECTED: Calling : " + phoneNumber);


            Handler handle = new Handler(getMainLooper());
            handle.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "ENTER", Toast.LENGTH_SHORT).show();
                }
            });


        } else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
            L.log("EXIT DETECTED");
            L.toast(this, "EXIT ! ");
        }
    }
}
