package com.bascii.triphistory;

import java.util.Comparator;

public class TripLastModifiedDateComparator implements Comparator<TripInfo> {
    @Override
    public int compare(TripInfo trip1, TripInfo trip2) {
        return trip2.LastModifiedDate.compareTo(trip1.LastModifiedDate);
    }
}