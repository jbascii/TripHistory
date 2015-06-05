package com.bascii.triphistory;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootInitializerForTracker extends BroadcastReceiver {
    public BootInitializerForTracker() {
    }

    private SharedPreferences mPrefs;
    public static final String TAG = "BootInit4Tracker";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");

        // Make sure we are getting the right intent
        if( "android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Log.i(TAG, "BOOT_COMPLETED");

            boolean isLocationsTracked = false;
            // Open the shared preferences
            //mPrefs = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);

            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

	        /*
	         * Get any previous setting for location updates
	         * Gets "false" if an error occurs
	         */
            if (mPrefs.contains(Constants.PREF_ISLOCATIONTRACKED)) {
                isLocationsTracked = mPrefs.getBoolean(Constants.PREF_ISLOCATIONTRACKED, false);
            }
            if(isLocationsTracked){
                Log.i(TAG, "Location tracking is REQUIRED");
                //ComponentName comp = new ComponentName(context.getPackageName(), TrackerService.class.getName());
                ComponentName comp = new ComponentName(context.getPackageName(), LocationUpdatesRequestService.class.getName());
                Intent myIntent = new Intent();
                intent.setComponent(comp);
                intent.putExtra(Constants.KEY_LOCATION_UPDATE_SERVICE_ACTION, Constants.LOCATION_UPDATES_SUBSCRIBE);
                ComponentName service = context.startService(myIntent);

                if (null == service){
                    // something really wrong here
                    Log.e(TAG, "Could not start service " + comp.toString());
                }
            }

        } else {
            Log.e(TAG, "Received unexpected intent " + intent.toString());
        }
    }
}
