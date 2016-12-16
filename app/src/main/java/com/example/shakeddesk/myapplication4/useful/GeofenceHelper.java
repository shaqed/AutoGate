package com.example.shakeddesk.myapplication4.useful;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.shakeddesk.myapplication4.location.GeofenceIntentService;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;

public class GeofenceHelper implements ResultCallback {

    private Context context;
    private PendingIntent pendingIntent;

    public GeofenceHelper(Context context) {
        this.context = context;
    }

    /**
     * Build a new Geofence object
     * */
    public Geofence createGeofence(String requestId, double latitude, double longtitude) {
        Geofence.Builder builder = new Geofence.Builder();
        builder.setRequestId(requestId);
        builder.setCircularRegion(latitude, longtitude, 500); // 500 meters radius
        builder.setExpirationDuration(Geofence.NEVER_EXPIRE);
        builder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
        return builder.build();
    }

    /**
     * Build the geofencing request object here (using the createGeofence() method)
     * */
    public GeofencingRequest getGeofencingRequest(Geofence geofenceObject) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofenceObject);
        return builder.build();
    }

    /**
     * Get PendingIntent
     * */
    public PendingIntent getGeofencePendingIntent() {
        if (this.pendingIntent == null) {
            Intent intent = new Intent(context, GeofenceIntentService.class);

            pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntent;
        } else {
            return this.pendingIntent;
        }
    }


    @Override
    public void onResult(@NonNull Result result) {
        L.log(result.getStatus().getStatusCode()+ "");
    }
}
