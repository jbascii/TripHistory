/*
package com.bascii.triphistory;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TripListActivity extends FragmentActivity {

    public static final String TAG = "TripListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);


        ArrayList<Map<String, String>> trips = TripManager.getInstance(this).getAllTrips();
        if(trips == null) {
            return;
        }

        ListView lv = (ListView) findViewById(R.id.lvTrips);
        */
/*ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_2,
                trips);*//*

*/
/*        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, trips) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(trips.get(position).getTripName());
                text2.setText(trips.get(position).getTripStartDate());
                return view;
            }
        };*//*


        String[] from = { "name", "date" };
        int[] to = { android.R.id.text1, android.R.id.text2 };
        SimpleAdapter adapter = new SimpleAdapter(this, trips,
                android.R.layout.simple_list_item_2, from, to);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                HashMap<String, String> trip = (HashMap<String, String>) parent.getItemAtPosition(position);
                String tripName = trip.get("name");

                Log.i(TAG, "Trip: " + tripName);

                Intent intent = new Intent(TripListActivity.this, TripActivity.class);
                intent.putExtra(Constants.KEY_TRIP_NAME, tripName);
                startActivity(intent);
            }
        });


    }



}
*/
