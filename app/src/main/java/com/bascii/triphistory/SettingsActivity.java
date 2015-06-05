/*
package com.bascii.triphistory;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;


//TODO: use PreferenceActivity
public class SettingsActivity extends  ActionBarActivity {

    public static final String TAG = "settings";

    private SharedPreferences mPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mPrefs = getSharedPreferences(Constants.PREF, MODE_PRIVATE);
        boolean isLocationsTracked = false;
        if(mPrefs.contains(Constants.PREF_ISLOCATIONTRACKED)) {
            isLocationsTracked = mPrefs.getBoolean(Constants.PREF_ISLOCATIONTRACKED, false);
        }

        CheckBox repeatChkBx = ( CheckBox ) findViewById( R.id.chkTrackLocations );
        repeatChkBx.setChecked(isLocationsTracked);

        repeatChkBx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                SharedPreferences.Editor ed = mPrefs.edit();
                ed.putBoolean(Constants.PREF_ISLOCATIONTRACKED, isChecked);
                ed.commit();

                if ( isChecked )
                {
                    startTrackingLocations();
                } else {
                    stopTrackingLocations();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();
        return super.onOptionsItemSelected(item);

    }

    private void startTrackingLocations() {
        Log.i(TAG, "start TrackingLocations");

        //Intent intent = new Intent(this, TrackerService.class);
        Intent intent = new Intent(this, LocationUpdatesRequestService.class);
        intent.putExtra(Constants.KEY_LOCATION_UPDATE_SERVICE_ACTION, Constants.LOCATION_UPDATES_SUBSCRIBE);
        this.startService(intent);
    }

    private void stopTrackingLocations() {
        Log.i(TAG, "stop TrackingLocations");

        Intent intent = new Intent(this, LocationUpdatesRequestService.class);
        intent.putExtra(Constants.KEY_LOCATION_UPDATE_SERVICE_ACTION, Constants.LOCATION_UPDATES_UNSUBSCRIBE);
        this.startService(intent);
    }
}
*/
