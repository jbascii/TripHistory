/*
package com.bascii.triphistory;

*/
/*
Credits:
https://gist.github.com/blackcj/20efe2ac885c7297a676
http://android-developers.blogspot.in/2011/06/deep-dive-into-location.html
https://code.google.com/p/android-protips-location/source/browse/trunk/src/com/radioactiveyak/location_best_practices/utils/FroyoLocationUpdateRequester.java
http://jcraane.blogspot.in/2014/01/android-location-based-services.html
http://davistechyinfo.blogspot.in/2014/09/android-location-updates-with.html

 *//*


//TODO: remove googleapiclient references and use  location manager/locationclient

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TrackerService extends Service implements
        LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener
{

    private static final String TAG = "TrackerService";

    IBinder mBinder = new LocalBinder();

    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    // Flag that indicates if a request is underway.
    private boolean mInProgress;

    private Boolean servicesAvailable = false;

    public class LocalBinder extends Binder {
        public TrackerService getServerInstance() {
            return TrackerService.this;
        }
    }

    public TrackerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "onCreate");
        mInProgress = false;

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();

        mLocationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER); // passive
        mLocationRequest.setInterval(Constants.UPDATE_INTERVAL); // Set the update interval to 5 seconds
        mLocationRequest.setFastestInterval(Constants.FASTEST_INTERVAL); // Set the fastest update interval to 1 second

        servicesAvailable = servicesConnected();

        Log.i(TAG, "SERVICES AVAILABLE : " + servicesAvailable);

        //Create a new location client, using the enclosing class to handle callbacks.
        mLocationClient = new LocationClient(this, this, this);

    }

    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {

            return true;
        } else {

            return false;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        if(!servicesAvailable || mLocationClient.isConnected() || mInProgress) {
            return START_STICKY;
        }

        setUpLocationClientIfNeeded();
        if(!mLocationClient.isConnected() || !mLocationClient.isConnecting() && !mInProgress){
            //appendLog(DateFormat.getDateTimeInstance().format(new Date()) + ": Started", Constants.LOG_FILE);
            Log.i(TAG, "STARTED");
            mInProgress = true;
            mLocationClient.connect();
        }

        return START_STICKY;
    }

    */
/*
     * Create a new location client, using the enclosing class to
     * handle callbacks.
     *//*

    private void setUpLocationClientIfNeeded()
    {
        if(mLocationClient == null)
            mLocationClient = new LocationClient(this, this, this);
    }

    public String getTime() {
        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return mDateFormat.format(new Date());
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestory");
        // Turn off the request flag
        mInProgress = false;

        if(servicesAvailable && mLocationClient != null) {
            mLocationClient.removeLocationUpdates(this);
            // Destroy the current location client
            mLocationClient = null;
        }

        // Display the connection status
        //appendLog(DateFormat.getDateTimeInstance().format(new Date()) + ": Stopped", Constants.LOG_FILE);
        Log.i(TAG, "STOPPED");
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected");
        // Request location updates using static settings

        mLocationClient.requestLocationUpdates(mLocationRequest, this);

        //appendLog(DateFormat.getDateTimeInstance().format(new Date()) + ": Connected", Constants.LOG_FILE);
        Log.i(TAG, "CONNECTED");
    }

    @Override
    public void onDisconnected() {
        Log.i(TAG, "onDisconnected");
        // Turn off the request flag
        mInProgress = false;

        // Destroy the current location client
        mLocationClient = null;

        // Display the connection status
        // Toast.makeText(this, DateFormat.getDateTimeInstance().format(new Date()) + ": Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        //appendLog(DateFormat.getDateTimeInstance().format(new Date()) + ": Disconnected", Constants.LOG_FILE);
        Log.i(TAG, "DISCONNECTED");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged");

        // Report to the UI that the location was updated
        String msg = Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());

        Log.e(TAG, "NEW LOCATION : " + msg);

        //store new location in current trip file
        TripManager.getInstance(this).storeNewLocation(location);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mInProgress = false;

        */
/*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         *//*

        if (connectionResult.hasResolution()) {

            // If no resolution is available, display an error dialog
        } else {

        }
    }

}*/
