package com.bascii.triphistory;

/*
Credits:
https://gist.github.com/blackcj/20efe2ac885c7297a676
http://android-developers.blogspot.in/2011/06/deep-dive-into-location.html
https://code.google.com/p/android-protips-location/source/browse/trunk/src/com/radioactiveyak/location_best_practices/utils/FroyoLocationUpdateRequester.java
http://jcraane.blogspot.in/2014/01/android-location-based-services.html
http://davistechyinfo.blogspot.in/2014/09/android-location-updates-with.html

 */

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationUpdatesRequestService extends Service implements
            GooglePlayServicesClient.ConnectionCallbacks,
            GooglePlayServicesClient.OnConnectionFailedListener
    {

    private static final String TAG = "LocUpdReqService";

    private IBinder mBinder = new LocalBinder();

    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    // Flag that indicates if a request is underway.
    private boolean mInProgress;
    private String _action;

    private Boolean servicesAvailable = false;

    public class LocalBinder extends Binder {
        public LocationUpdatesRequestService getServerInstance() {
            return LocationUpdatesRequestService.this;
        }
    }

    public LocationUpdatesRequestService() {
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
        super.onStartCommand(intent, flags, startId);

        Log.i(TAG, "onStartCommand");

        if (intent != null && intent.getExtras() != null) {
            _action = intent.getExtras().getString(Constants.KEY_LOCATION_UPDATE_SERVICE_ACTION);
        }

        if(!servicesAvailable || mLocationClient.isConnected() || mInProgress) {
            return START_STICKY;
        }

        setUpLocationClientIfNeeded();
        if(!mLocationClient.isConnected() || !mLocationClient.isConnecting() && !mInProgress){
            mInProgress = true;
            mLocationClient.connect();
        }

        return START_STICKY;
    }

    /*
     * Create a new location client, using the enclosing class to
     * handle callbacks.
     */
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
            /////mLocationClient.removeLocationUpdates(this);

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

        //use
        //mLocationClient.requestLocationUpdates(mLocationRequest, this);
        Intent intent = new Intent(this, LocationChangedReceiver.class);

        if(Constants.LOCATION_UPDATES_SUBSCRIBE.equals(_action)) {
            PendingIntent locationIntent = PendingIntent.getBroadcast(getApplicationContext(),  0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mLocationClient.requestLocationUpdates(mLocationRequest, locationIntent);

            Log.i(TAG, "SUBSCRIBED to location updates");

        } else if(Constants.LOCATION_UPDATES_UNSUBSCRIBE.equals(_action)) {
            PendingIntent locationIntent = PendingIntent.getBroadcast(getApplicationContext(),  0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            mLocationClient.removeLocationUpdates(locationIntent);

            Log.i(TAG, "UNSUBSCRIBED from location updates");
        }

        //stop the service
        this.stopSelf();
    }

    @Override
    public void onDisconnected() {
        Log.i(TAG, "onDisconnected");
        // Turn off the request flag
        mInProgress = false;

        // Destroy the current location client
        mLocationClient = null;

        // Display the connection status
        Log.i(TAG, "DISCONNECTED");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed");

        mInProgress = false;

        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {

            // If no resolution is available, display an error dialog
        } else {

        }

        this.stopSelf();
    }

}
