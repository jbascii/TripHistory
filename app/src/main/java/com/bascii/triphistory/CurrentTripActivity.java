package com.bascii.triphistory;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class CurrentTripActivity extends FragmentActivity {

    public static final String TAG = "currenttripactivity";

    private String tripName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_trip);

        Intent intent = this.getIntent();
        if(null == intent) {
            return;
        }

        tripName = intent.getStringExtra(Constants.KEY_TRIP_NAME);
        Log.i(TAG, "TRIP NAME: " + tripName);
        //currentTrip = TripManager.getInstance(this).getCurrentTrip();

        TextView txtTripName = ( TextView ) findViewById( R.id.txtTripName );
        if(tripName == null) {
            txtTripName.setText("No Current Trip");
            return;
        }

        txtTripName.setText(tripName);


        ListView lv = (ListView) findViewById(R.id.lvTripLocations);

        List<String> tripLocations = new ArrayList<String>();
        FileInputStream in;

        try {
            in = openFileInput(tripName + Constants.TRIP_FILE_EXTENSION);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "Trip file not found. " + tripName);
            return;
        }

        InputStreamReader inputStreamReader = new InputStreamReader(in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                tripLocations.add(line);
            }
        } catch(IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error reading trip file");
        }


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                tripLocations);

        lv.setAdapter(arrayAdapter);
    }



}
