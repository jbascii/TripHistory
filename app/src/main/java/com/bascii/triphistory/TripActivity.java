package com.bascii.triphistory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class TripActivity extends ActionBarActivity {

    public static final String TAG = "TripActivity";
    private String tripName;
    private String tripFileName;
    private GoogleMap map;
    private ActionBar actionBar;

    private boolean isActiveTrip = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        actionBar = getSupportActionBar();

        //actionBar.setLogo(R.mipmap.ic_launcher);
        //actionBar.setDisplayUseLogoEnabled(true);
        //actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = this.getIntent();
        if(null == intent) {
            return;
        }

        tripName = intent.getStringExtra(Constants.KEY_TRIP_NAME);
        tripFileName = tripName + Constants.TRIP_FILE_EXTENSION;
        Log.i(TAG, "TRIP NAME: " + tripName);


        isActiveTrip = TripManager.getInstance(this).isActive(tripName);
        actionBar.setTitle(tripName);

        this.invalidateOptionsMenu();

        //ListView lv = (ListView) findViewById(R.id.lvTripLocations);

        ArrayList<LatLng> tripLocations = new ArrayList<LatLng>();
        FileInputStream in;

        try {
            in = openFileInput(tripFileName);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "Trip file not found. " + tripFileName);
            return;
        }

        InputStreamReader inputStreamReader = new InputStreamReader(in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        LatLng prev = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                if(line.startsWith("WP:")) {

                    double lat=0, lng=0;
                    long time;

                    line = line.replace("WP:","");
                    Log.i(TAG, line);
                    String[] lineData = line.split(",");

                    lat = Double.parseDouble(lineData[0]);
                    lng = Double.parseDouble(lineData[1]);
                    //time = Long.parseLong(lineData[2]);

                    LatLng wp = new LatLng(lat, lng);
                    Log.i(TAG, "WAYPOINT:" + wp.toString());

                    if(prev != null && prev.equals(wp)) {
                        Log.d(TAG, "duplicate wp");
                    } else {
                        tripLocations.add(wp);
                        prev = wp;
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error reading trip file");
        }

        int totalWayPoints = tripLocations.size();
        Log.i(TAG, "Number of waypoints recorded in trip: " + totalWayPoints);
        if(totalWayPoints < 1) {
            return;
        }

        LatLng START_LOCATION = tripLocations.get(0);

        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
        //map.setMyLocationEnabled(true);

        map.addMarker(new MarkerOptions().position(START_LOCATION).title("Start")
                .snippet("Started Here"));

        if(totalWayPoints > 2) {
            for(int i=1;i<totalWayPoints-1;i++) {
                map.addMarker(new MarkerOptions().position(tripLocations.get(i)));
            }
        }

        LatLng LAST_LOCATION = null;
        if(totalWayPoints > 1) {
            LAST_LOCATION = tripLocations.get(totalWayPoints-1); //get the last point

            map.addMarker(new MarkerOptions().position(LAST_LOCATION).title("Finish")
                    .snippet("Finished Here"));
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(START_LOCATION, 40));
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip, menu);

        MenuItem itemEndTrip = menu.findItem(R.id.action_end_trip);
        itemEndTrip.setVisible(isActiveTrip);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_rename_trip:
                renameTrip();
                return true;

            case R.id.action_end_trip:
                endCurrentTrip();
                return true;

            case R.id.action_delete_trip:
                deleteTrip();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void endCurrentTrip() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("End this trip?");

        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                TripManager.getInstance(TripActivity.this).endCurrentTrip();
                isActiveTrip = false;
                TripActivity.this.invalidateOptionsMenu();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.cancel();
            }
        });

        dialog.show();
    }

    private void renameTrip() {
        Log.i(TAG, "RENAME TRIP clicked");

        final TripManager tripManager = TripManager.getInstance(this);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Rename");

        final EditText newNameInput = new EditText(this);
        newNameInput.setBackgroundColor(Color.WHITE);
        newNameInput.setTextColor(Color.BLACK);
        newNameInput.setText(tripName);
        newNameInput.setPadding(10, 10, 10, 10);
        dialog.setView(newNameInput);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
            //rename the file
            String newTripName = newNameInput.getText().toString();

            if(tripManager.renameTrip(tripName, newTripName)) {
                Log.i(TAG, "RENAMED ");

                tripName = newTripName;
                actionBar.setTitle(tripName);

            } else {
                Log.e(TAG, "Rename FAILED - " + newTripName);
            }
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                arg0.cancel();
            }
        });

        dialog.show();
    }

    private void deleteTrip() {
        Log.i(TAG, "DELETE TRIP clicked");

        final TripManager tripManager = TripManager.getInstance(this);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Discard Trip?");
        dialog.setMessage("All locations recorded on this trip will be lost!");

        dialog.setPositiveButton("Discard", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
            if(tripManager.deleteTrip(tripName)) {
                Log.i(TAG, "DELETED ");

                //navigate to home screen
                finish();
            } else {
                Log.e(TAG, "unable to delete");
            }
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                arg0.cancel();
            }
        });

        dialog.show();
    }
}
