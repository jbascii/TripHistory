package com.bascii.triphistory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationClient;

public class LocationChangedReceiver extends BroadcastReceiver {
    private static final String TAG = "LocationChangedReceiver";

    public LocationChangedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");

        Location location = (Location) intent.getExtras().get(LocationClient.KEY_LOCATION_CHANGED);
        if(location == null) {
            Log.i(TAG, "onReceive: location object empty!!!");
            return;
        }

        // Report to the UI that the location was updated
        String msg = Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());

        Log.e(TAG, "NEW LOCATION : " + msg);

        //store new location in current trip file
        TripManager.getInstance(context).storeNewLocation(location);
    }
}
