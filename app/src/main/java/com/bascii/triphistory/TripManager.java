package com.bascii.triphistory;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class TripManager {

    public static final String TAG = "TripManager";



    private Context context;
    private String currentTripFileName;
    private SharedPreferences mPrefs;

    private static final TripManager _manager = new TripManager();
    private TripManager () {

    }

    public static TripManager getInstance(Context ctx) {
        _manager.context = ctx;
        _manager.ensureCurrentTrip();
        return _manager;
    }

    private void ensureCurrentTrip() {
        Log.i(TAG, "ensureCurrentTrip");

        if(currentTripFileName == null) {
            Log.i(TAG, "no current trip in variable");

            //check whether there is an active trip
            //mPrefs = context.getSharedPreferences(Constants.PREF, context.MODE_PRIVATE);
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

            if(mPrefs.contains(Constants.PREF_CURRENT_TRIP_FILENAME)) {
                Log.i(TAG, "current trip available");
                currentTripFileName = mPrefs.getString(Constants.PREF_CURRENT_TRIP_FILENAME, "");
            } else {
                Log.i(TAG, "no current trip, starting new trip");

                //if not, start new trip (only if auto start flag is set)
                boolean autoStartNewTrip = mPrefs.getBoolean(Constants.PREF_AUTO_START_NEW_TRIP, false);
                if(autoStartNewTrip) {
                    _manager.startNewTrip(null); //new trip, with default name
                }
            }
        }
    }

    public boolean isActive(String tripName) {
        return tripName.equals(this.getCurrentTrip());
    }

    public String getCurrentTrip() {
        Log.i(TAG, "getCurrentTrip");

        if(currentTripFileName == null) {
            Log.i(TAG, "no current trip in variable");

            //see if there is active trip
            //mPrefs = context.getSharedPreferences(Constants.PREF, context.MODE_PRIVATE);
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

            if(mPrefs.contains(Constants.PREF_CURRENT_TRIP_FILENAME)) {
                Log.i(TAG, "current trip available");
                currentTripFileName = mPrefs.getString(Constants.PREF_CURRENT_TRIP_FILENAME, "");
            } else {
                Log.i(TAG, "no current trip");
            }
        }

        if(currentTripFileName != null) {
            //remove the file extension and return the name
            return currentTripFileName.replace(Constants.TRIP_FILE_EXTENSION, "");
        } else {
            return null;
        }
    }

    public boolean startNewTrip(String givenTripName) {
        Log.i(TAG, "startNewTrip");

        Calendar c = Calendar.getInstance();
        Date startTime = c.getTime();

        Log.d(TAG, "Current time => " + startTime.getTime()); //long

        //long unixTime = System.currentTimeMillis() / 1000L;
        //Log.d(TAG, "unix time => " + unixTime);

        String tripFileName;
        if(givenTripName == null) { //if trip name is not provided, create with default name
            //get default trip file name
            String formattedDate = DateFormat.getDateTimeInstance().format(startTime);
            tripFileName = Constants.TRIP_FILE_PREFIX_DEFAULT + formattedDate +  Constants.TRIP_FILE_EXTENSION;
        } else {
            tripFileName = givenTripName +  Constants.TRIP_FILE_EXTENSION;
        }

        try {
            FileOutputStream fos = context.openFileOutput(tripFileName, Context.MODE_APPEND);

            currentTripFileName = tripFileName; //now that file is created, set current trip file name

            String msg = "VERSION:" + Constants.TRIP_FILE_FORMAT_VERSION;
            fos.write(msg.getBytes());
            fos.write('\n');



            msg = "STARTDATE:" + startTime.getTime(); //long
            fos.write(msg.getBytes());
            fos.write('\n');

            fos.close();

            Log.i(TAG, "NEW TRIP started");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to start new trip");
            return false;

        }catch(IOException e){
            e.printStackTrace();
            Log.e(TAG, "Unable to start new trip");
            return false;
        }

        //store it in pref
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putString(Constants.PREF_CURRENT_TRIP_FILENAME, currentTripFileName);
        ed.commit();

        return true;
    }

    public boolean endCurrentTrip() {
        Log.i(TAG, "endCurrentTrip");

        if(currentTripFileName == null) {
            Log.e(TAG, "No trip active!!!");
            return false;
        }

        Calendar c = Calendar.getInstance();
        Date endTime = c.getTime();

        Log.d(TAG, "Current time => " + endTime.getTime()); //long

        try {
            FileOutputStream fos = context.openFileOutput(currentTripFileName, Context.MODE_APPEND);

            String msg = "STARTDATE:" + endTime.getTime(); //long
            fos.write(msg.getBytes());
            fos.write('\n');

            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "Current trip file not found");
            return false;

        }catch(IOException e){
            e.printStackTrace();
            Log.e(TAG, "Unable to open current trip file");
            return false;
        }

        this.resetActiveTrip();

        Log.i(TAG, "Current trip ended");
        return true;
    }

    public boolean storeNewLocation(Location location) {

        if(currentTripFileName == null) {
            //no active trip available, quit
            Log.i(TAG, "No active trip available to store new location");
            return false;
        }

        String lat = Double.toString(location.getLatitude());
        String lng = Double.toString(location.getLongitude());

        //ignore if new location is same as previous location
        if(mPrefs.contains(Constants.CURRENT_TRIP_PREV_LOCATION_LAT)) {
            String prevWPLat = mPrefs.getString(Constants.CURRENT_TRIP_PREV_LOCATION_LAT, "");
            String prevWPLng = mPrefs.getString(Constants.CURRENT_TRIP_PREV_LOCATION_LNG, "");

            //is same location as previous?
            if(lat.equals(prevWPLat) && lng.equals(prevWPLng)) {
                //yes, then ignore
                Log.i(TAG, "Same as previous location. Ignoring!!!");
                return true;
            }
        }


        //get comma separated string
        //Note: location.getTime is long. do NOT format to date time
        String msg = "WP:" + lat + "," + lng + ", " + location.getTime();

        try {

            FileOutputStream fos = context.openFileOutput(currentTripFileName, Context.MODE_APPEND);
            fos.write(msg.getBytes());
            fos.write('\n');
            fos.close();

            Log.i(TAG, "NEW LOCATION recorded");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "unable to record NEW LOCATION");
            return false;

        }catch(IOException e){
            e.printStackTrace();
            Log.e(TAG, "unable to record NEW LOCATION");
            return false;
        }

        //overwrite last location
        SharedPreferences.Editor ed = mPrefs.edit();
        //note: Double type not support!!!
        ed.putString(Constants.CURRENT_TRIP_PREV_LOCATION_LAT, lat);
        ed.putString(Constants.CURRENT_TRIP_PREV_LOCATION_LNG, lng);
        ed.commit();

        return true;
    }

    public String getTripFolderPath() {
        return context.getFilesDir().getAbsolutePath() + "/";
    }

    public String getDefaultNewTripName() {
        //trip name, not file name i.e. without extension
        Calendar c = Calendar.getInstance();
        Date startTime = c.getTime();

        Log.d(TAG, "Current time => " + startTime.getTime()); //long

        //long unixTime = System.currentTimeMillis() / 1000L;
        //Log.d(TAG, "unix time => " + unixTime);

        String formattedDate = DateFormat.getDateTimeInstance().format(startTime);

        //get default trip name
        return Constants.TRIP_FILE_PREFIX_DEFAULT + formattedDate;
    }

    //Map<String, String>
    public ArrayList<TripInfo> getAllTrips() {
        ArrayList<TripInfo> list = new ArrayList<TripInfo>();

        String path = context.getFilesDir().getAbsolutePath() + "/";
        Log.i(TAG, "internal folder path : " + path);

        //TODO: sort by last modified date
        for(String tripFileName : context.fileList()) {
            //is it trip data file?
            if(tripFileName.endsWith(Constants.TRIP_FILE_EXTENSION)) {
                File file = new File(path + tripFileName);
                TripInfo trip = new TripInfo();

                trip.Name = tripFileName.replace(Constants.TRIP_FILE_EXTENSION, "");
                trip.LastModifiedDate = new Date(file.lastModified());

                //String dt = DateFormat.getDateTimeInstance().format(lastModified);
                //list.add(putData(name, dt));
                list.add(trip);
            }
        }

        Collections.sort(list, new TripLastModifiedDateComparator ());

        //return context.fileList();
        return list;
    }

    public boolean renameTrip(String tripName, String tripNewName) {
        String tripFolder = this.getTripFolderPath();
        String tripFileName = tripName + Constants.TRIP_FILE_EXTENSION;
        File tripFile = new File(tripFolder, tripFileName);

        String newFileName = tripNewName + Constants.TRIP_FILE_EXTENSION;

        //remember whether trip was active before renaming
        boolean activeTrip = this.isActive(tripName);

        //rename trip file
        boolean renamed = tripFile.renameTo(new File(tripFolder, newFileName));

        //if active trip was renamed, set current trip variables
        if(renamed && activeTrip) {
            Log.i(TAG, "active trip was renamed");

            currentTripFileName = newFileName;

            //store it in pref
            SharedPreferences.Editor ed = mPrefs.edit();
            ed.putString(Constants.PREF_CURRENT_TRIP_FILENAME, currentTripFileName);
            ed.commit();
        }

        return renamed;
    }

    public boolean deleteTrip(String tripName) {
        String tripFolder = this.getTripFolderPath();
        String tripFileName = tripName + Constants.TRIP_FILE_EXTENSION;
        File tripFile = new File(tripFolder, tripFileName);

        boolean deleted = false;
        if(this.isActive(tripName)) {
            Log.i(TAG, "active trip is being deleted");

            this.resetActiveTrip();
        }

        deleted = tripFile.delete();
        return deleted;
    }

    /*private HashMap<String, String> putData(String name, String date) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("name", name);
        item.put("date", date);
        return item;
    }*/

    private void resetActiveTrip() {
        //clear current trip variables
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putString(Constants.PREF_CURRENT_TRIP_FILENAME, null);
        ed.commit();

        currentTripFileName = null;
    }
}
