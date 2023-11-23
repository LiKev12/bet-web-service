package com.bet.betwebservice.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Helpers {

    public static String getDatetimeFromTimestamp(Integer timestamp) {
        if (timestamp == null) {
            return null;
        }
        Date date = new Date((long) timestamp * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        return sdf.format(date);
    }
}
