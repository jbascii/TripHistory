package com.bascii.triphistory;

public final class Constants {

    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    private static final int UPDATE_INTERVAL_IN_SECONDS = 30;
    // Update frequency in milliseconds
    public static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 30;
    // A fast frequency ceiling in milliseconds
    public static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    // Stores the lat / long pairs in a text file
    public static final String LOCATION_FILE = "sdcard/location.txt";
    // Stores the connect / disconnect data in a text file
    public static final String LOG_FILE = "sdcard/log.txt";



    //preferences/settings keys
    public static final String PREF = "trackerservice";
    public static final String PREF_ISLOCATIONTRACKED = "islocationtracked";
    public static final String PREF_AUTO_START_NEW_TRIP = "autostartnewtrip";
    public static final String PREF_WARN_BEFORE_NEW_TRIP = "warnbeforenewtrip";

    public static final String PREF_CURRENT_TRIP_FILENAME = "currenttripfilename";


    public static final String KEY_LOCATION_UPDATE_SERVICE_ACTION = "action";
    public static final String LOCATION_UPDATES_SUBSCRIBE = "subscribe";
    public static final String LOCATION_UPDATES_UNSUBSCRIBE = "unsubscribe";

    public static final String KEY_TRIP_NAME = "tripname";

    public static final String TRIP_FILE_PREFIX_DEFAULT = "Trip - ";
    public static final String TRIP_FILE_EXTENSION = ".trip";
    public static final String TRIP_FILE_FORMAT_VERSION = "1";

    public static final String CURRENT_TRIP_PREV_LOCATION_LAT = "CT_PREV_LAT";
    public static final String CURRENT_TRIP_PREV_LOCATION_LNG = "CT_PREV_LNG";


    /**
     * Suppress default constructor for noninstantiability
     */
    private Constants() {
        throw new AssertionError();
    }
}