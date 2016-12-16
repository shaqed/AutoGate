package com.example.shakeddesk.myapplication4;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.shakeddesk.myapplication4.location.LocationSelectActivity;
import com.example.shakeddesk.myapplication4.useful.GeofenceHelper;
import com.example.shakeddesk.myapplication4.useful.L;
import com.example.shakeddesk.myapplication4.useful.SharedPreferenceHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class LocationListenActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    public static final int REQUEST_COORDS = 10; // Request code for coordinates
    public static final int REQUEST_CONTACT = 11; // Request code for contact

    private GoogleApiClient googleApiClient;

    private TextView latTV,longTv;
    private Button startListeningButton, stopListeningButton, startGeofencingButton, stopGeofencingButton;

    private Button selectCoordsButton, selectContactButton;
    private TextView geofenceLatTv, geofenceLongTv, contactTv;
    private boolean listening;

    private String contactPhoneNumber = null;
    private LatLng geofencingLatLng = null;


    GeofenceHelper geofenceHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_listen);
        initViews(); // IMPORTANT !! not calling this causes nullPointerException

        geofenceHelper = new GeofenceHelper(this);
        updateDataFromPref();

        startListeningButton.setOnClickListener(this);
        stopListeningButton.setOnClickListener(this);

        startGeofencingButton.setOnClickListener(this);
        stopGeofencingButton.setOnClickListener(this);

        selectContactButton.setOnClickListener(this);
        selectCoordsButton.setOnClickListener(this);

    }

    private void initViews() {
        startListeningButton = (Button) findViewById(R.id.startListenBtn);
        stopListeningButton = (Button) findViewById(R.id.stopListenBtn);

        latTV = (TextView) findViewById(R.id.latTV);
        longTv = (TextView) findViewById(R.id.longTV);

        startGeofencingButton = (Button) findViewById(R.id.startGeoFencingBtn);
        stopGeofencingButton = (Button) findViewById(R.id.stopGeofencingBtn);

        selectCoordsButton = (Button) findViewById(R.id.locationListenSelectNewCordsButton);
        selectContactButton = (Button) findViewById(R.id.locationListenSelectContact);

        geofenceLatTv = (TextView) findViewById(R.id.locationListenGeofenceLatDisplay);
        geofenceLongTv = (TextView) findViewById(R.id.locationListenGeofenceLongDisplay);
        contactTv = (TextView) findViewById(R.id.locationListenContactDisplay);
    }

    private void updateDataFromPref(){
        SharedPreferenceHandler handler = new SharedPreferenceHandler(this);
        LatLng latLng = handler.getLatLng();
        if (latLng != null) {
            this.geofenceLatTv.setText(latLng.latitude+"");
            this.geofenceLongTv.setText(latLng.longitude+"");
            this.geofencingLatLng = latLng;
        } else {
            L.log("No LatLng was store in preferences");
        }

        String phoneNumber = handler.getPhoneNumber();
        if (phoneNumber != null) {
            this.contactTv.setText(phoneNumber);
            this.contactPhoneNumber = phoneNumber;
        } else {
            L.log("No Contact was saved in preferences");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        L.log();
        getGoogleApiClient();
        apiClientConnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        L.log();
        updateListening(new SharedPreferenceHandler(this).getBooleanValue());
    }

    @Override
    protected void onStop() {
        super.onStop();
        L.log();
        stopGenfencing();
        stopListening();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_COORDS:
                try {
                    LatLng latLng = data.getParcelableExtra(LatLng.class.getSimpleName());
                    L.toast(this, "Receives coords successfully. lat: " + latLng.latitude + " long: " + latLng.longitude);
                    this.geofenceLatTv.setText("" + latLng.latitude);
                    this.geofenceLongTv.setText(""+latLng.longitude);

                    SharedPreferenceHandler handler = new SharedPreferenceHandler(this);
                    handler.storeLatLng(latLng);
                    updateDataFromPref();
                } catch (Exception e) {
                    L.toast(this, "Could not get coords... reason: " + e.getMessage());
                }
                break;
            case REQUEST_CONTACT:
                if (resultCode == RESULT_OK) {
                    // Google's code for getting contact number
                    Uri contactUri = data.getData();
                    String[] proj = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                    Cursor cursor = getContentResolver()
                            .query(contactUri, proj, null, null, null);
                    cursor.moveToFirst();

                    // Retrieve the phone number from the NUMBER column
                    int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(column);

                    cursor.close();
                    L.toast(this, "Got a number: " + number);
                    this.contactTv.setText(number);

                    SharedPreferenceHandler handler = new SharedPreferenceHandler(this);
                    handler.storePhoneNumber(number);
                    updateDataFromPref();
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.startListenBtn:
                startListening();
                break;
            case R.id.stopListenBtn:
                stopListening();
                break;
            case R.id.startGeoFencingBtn:
                startGeofencing();
                break;
            case R.id.stopGeofencingBtn:
                stopGenfencing();
                break;
            case R.id.locationListenSelectNewCordsButton:
                startActivityForResult(new Intent(this, LocationSelectActivity.class),REQUEST_COORDS);
                break;
            case R.id.locationListenSelectContact:
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(pickContactIntent, REQUEST_CONTACT);
                break;
            default:
                L.log("Unexpected view called on the onClickListener");
                break;
        }
    }




    /* LOCATION LISTENER */
    private void startListening(){
        try {
            if (googleApiClient != null) {
                this.apiClientConnect();
                if (googleApiClient.isConnected()) {
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(10 * 1000); // 10 seconds interval
                    locationRequest.setFastestInterval(5 * 1000); // 5 seconds interval
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                    L.log("starting to listen to location changes");
                    updateListening(true);
                } else {
                    L.log("googleApi is not connected... isConnecting(): " + googleApiClient.isConnecting());
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            L.log("Failed, no permission given");
        }
    }

    private void stopListening() {
        try {
            if (googleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                this.apiClientDisconnect();
                L.log("Stopped listening to location updates");
                updateListening(false);
            }
        } catch (Exception e) {
            L.log("Cannot stop listening: reason: " + e.getMessage());
        }
    }

    private void updateListening(boolean listening) {
        startListeningButton.setEnabled(!listening);
        stopListeningButton.setEnabled(listening);
        new SharedPreferenceHandler(this).storeValue(listening);
        this.listening = listening;
    }
    /* LOCATION LISTENER END */


    /* GEOFENCING */
    private void startGeofencing() {
        try {

            LocationServices.GeofencingApi.addGeofences(googleApiClient,
                    geofenceHelper.getGeofencingRequest(geofenceHelper.createGeofence("req1", this.geofencingLatLng.latitude, this.geofencingLatLng.longitude)),
                    geofenceHelper.getGeofencePendingIntent()).setResultCallback(geofenceHelper);

            L.log("starting to geofence");
            updateGeofencing(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void stopGenfencing() {
        try {
            LocationServices.GeofencingApi.removeGeofences(googleApiClient, geofenceHelper.getGeofencePendingIntent());
            L.log("Stopped geofencing");
            updateGeofencing(false);
        } catch (Exception e) {
            L.log("Cannot stop geofencing: " + e.getMessage());
        }
    }

    private void updateGeofencing(boolean geofencing) {
        startGeofencingButton.setEnabled(!geofencing);
        stopGeofencingButton.setEnabled(geofencing);
    }
    /* GEOFENCING END */


    /* GOOGLE API CLIENT */
    private GoogleApiClient getGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            L.log("googleApiClient was initialized");
        }
        return googleApiClient;
    }

    private void apiClientConnect() {
        if (googleApiClient != null) {
            googleApiClient.connect();
            L.log("API client was connected");
        }
    }

    private void apiClientDisconnect() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
            L.log("API client was disconnected");
        }
    }
    /* END OF GOOGLE API CLIENT */


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        L.log();
    }

    @Override
    public void onConnectionSuspended(int i) {
        L.log();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        L.log("xxx: " + connectionResult.getErrorCode());
        Dialog dialog =GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 1);
        if (dialog != null) {
            dialog.show();
        }
        L.log();
    }

    @Override
    public void onLocationChanged(Location location) {
        L.log();
        latTV.setText(location.getLatitude()+"");
        longTv.setText(location.getLongitude() + "");
    }



}
