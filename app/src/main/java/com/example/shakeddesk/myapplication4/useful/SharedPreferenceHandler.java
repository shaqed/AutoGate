package com.example.shakeddesk.myapplication4.useful;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

public class SharedPreferenceHandler {
    private Context context;
    private static final String TAG_PREF = "MyPref";

    private static final String TAG_BOOLEAN = "Listening";
    private static final String TAG_LATLNG_LAT = "LatLngLatitude";
    private static final String TAG_LATLNG_LONG = "LatLngLongtitude";

    private static final String TAG_CONTACT_PHONE = "Phone";

    public SharedPreferenceHandler(Context context) {
        this.context = context;
    }

    public void storeValue(boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(TAG_BOOLEAN, value).apply();
        L.log("Stored value: " + value + " under tag: " + TAG_BOOLEAN + " in shared preferences");
    }


    /**
     * @return CAUTION! default return value is false. so if your value is false and you didn't store it -
     * Expect that this is an error
     * */
    public boolean getBooleanValue() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(TAG_BOOLEAN, false);
//        return false;
    }

    public void storeLatLng(LatLng latLng) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(TAG_LATLNG_LAT, (float) latLng.latitude);
        editor.putFloat(TAG_LATLNG_LONG, (float) latLng.longitude);
        editor.apply();
    }
    public LatLng getLatLng() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        float lat = sharedPreferences.getFloat(TAG_LATLNG_LAT, -1);
        float lon = sharedPreferences.getFloat(TAG_LATLNG_LONG, -1);
        if (lat == -1 || lon == -1){
            L.log("Could not find lat or long in shared preferences... returning null");
            L.log("Lat: " + lat);
            L.log("Long: " + lon);
            return null;
        } else {
            return new LatLng(lat,lon);
        }
    }

    public void storePhoneNumber(String phoneNumber) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_CONTACT_PHONE, phoneNumber);
        editor.apply();
    }

    public String getPhoneNumber() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String res = sharedPreferences.getString(TAG_CONTACT_PHONE, null);
        if (res == null){
            L.log("Could not find phone number... returning null");
        }
        return res;
    }
}
