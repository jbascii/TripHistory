package com.bascii.triphistory;


import java.text.DateFormat;
import java.util.Date;

public class TripInfo {

    public String Name;

    public Date LastModifiedDate;

    public String FormattedLastModifiedDate() {
        if(LastModifiedDate != null) {
            return DateFormat.getDateTimeInstance().format(LastModifiedDate);
        } else {
            return "";
        }
    }
}


