package com.bascii.triphistory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

/*
https://stackoverflow.com/questions/26509180/no-actionbar-in-preferenceactivity-after-upgrade-to-support-library-v21
 */
public class Settings2Activity
        extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = "settings";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        //getActionBar().setDisplayShowTitleEnabled(true);
        setTitle(R.string.title_activity_settings2);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Toolbar bar;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
            root.addView(bar, 0); // insert at top
        } else {
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);

            root.removeAllViews();

            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);


            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }else{
                height = bar.getHeight();
            }

            content.setPadding(0, height, 0, 0);

            root.addView(content);
            root.addView(bar);
        }

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        Log.i(TAG, key);

        if (key.equals(Constants.PREF_ISLOCATIONTRACKED)) {
            boolean isLocationsTracked = prefs.getBoolean(Constants.PREF_ISLOCATIONTRACKED, false);

            if(isLocationsTracked) {
                startTrackingLocations();
            } else {
                stopTrackingLocations();
            }
        }
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
