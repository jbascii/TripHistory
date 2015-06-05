package com.bascii.triphistory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;


//TODO: check http://android-developers.blogspot.in/2015/04/android-support-library-221.html
public class MainActivity extends ActionBarActivity  {
    public static final String TAG = "MainActivity";

    @Override
    protected void onStart() {
        super.onStart();

        Log.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "onResume");
        ArrayList<TripInfo> trips = TripManager.getInstance(this).getAllTrips();
        if(trips == null) {
            return;
        }

        ListView lv = (ListView) findViewById(R.id.lvTrips);

/*
        String[] from = { "name", "date" };
        int[] to = { android.R.id.text1, android.R.id.text2 };
*/

        /*android.widget.ArrayAdapter<TripInfo> adapter = new ArrayAdapter<TripInfo>(this,
                android.R.layout.simple_list_item_2, [T] trips, );*/

        TripsAdapter adapter = new TripsAdapter(this, trips);
/*
        SimpleAdapter adapter = new SimpleAdapter(this, trips,
                android.R.layout.simple_list_item_2, from, to);
*/

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //HashMap<String, String> trip = (HashMap<String, String>) parent.getItemAtPosition(position);
                TripInfo trip = (TripInfo) parent.getItemAtPosition(position);
                //String tripName = trip.get("name");
                String tripName = trip.Name;

                Log.i(TAG, "Trip: " + tripName);

                Intent intent = new Intent(MainActivity.this, TripActivity.class);
                intent.putExtra(Constants.KEY_TRIP_NAME, tripName);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //set default values for preferences (but do not overwrite if already set)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_newtrip:
                startNewTrip();
                return true;

            case R.id.action_settings:
                showSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showSettings() {
        Intent myIntent = new Intent(this, Settings2Activity.class);
        this.startActivity(myIntent);
    }

    public void startNewTrip() {

        final TripManager tm = TripManager.getInstance(this);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean warnBeforeNewTrip = prefs.getBoolean(Constants.PREF_WARN_BEFORE_NEW_TRIP, false);

        if(warnBeforeNewTrip) {
            //check whether there is active trip
            if(tm.getCurrentTrip() != null) {
                dialog.setTitle("Stop Current Trip ?");
                dialog.setMessage("Current trip needs to be stopped before starting new trip!");

                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if(tm.endCurrentTrip()) {
                            Log.i(TAG, "Stopped");
                            getNameAndStartNewTrip();
                        } else {
                            Log.e(TAG, "unable to stop");
                            //show message
                            Toast.makeText(MainActivity.this, "Unable to stop current trip!", Toast.LENGTH_SHORT).show();
                            arg0.cancel();
                        }
                    }
                });

                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.cancel();
                    }
                });

                dialog.show();
            }
        } else {
            getNameAndStartNewTrip();
        }
    }

    private void getNameAndStartNewTrip() {
        final TripManager tm = TripManager.getInstance(this);
        final String defaultNewTripName = tm.getDefaultNewTripName();

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("New Trip Name");

        final EditText newNameInput = new EditText(this);
        newNameInput.setBackgroundColor(Color.WHITE);
        newNameInput.setTextColor(Color.BLACK);
        newNameInput.setText(defaultNewTripName);
        newNameInput.setPadding(10, 10, 10, 10);
        dialog.setView(newNameInput);


        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                //rename the file
                String newTripName = newNameInput.getText().toString();
                newTripName = newTripName.trim();
                if(newTripName.length() == 0) {
                    Toast.makeText(MainActivity.this, "Trip name cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //start new trip with given name
                if(tm.startNewTrip(newTripName)) {
                    Toast toast = Toast.makeText(MainActivity.this, "New Trip Started!", Toast.LENGTH_SHORT);
                    toast.show();

                    //redirect to Trip details
                    Intent intent = new Intent(MainActivity.this, TripActivity.class);
                    intent.putExtra(Constants.KEY_TRIP_NAME, newTripName);
                    startActivity(intent);
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

    /*public void showTripList(View view) {
        Intent myIntent = new Intent(this, TripListActivity.class);
        this.startActivity(myIntent);
    }*/

    /*public void showCurrentTrip(View view) {
        String currentTripId = TripManager.getInstance(this).getCurrentTrip();

        Intent myIntent = new Intent(this, CurrentTripActivity.class);
        myIntent.putExtra(Constants.KEY_TRIP_NAME, currentTripId);
        this.startActivity(myIntent);
    }*/
}
