package uk.co.softsapps.weatherforecast.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Fernando Bonet on 23/07/2017.
 */

public class DateHelper {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;

    public static final String PATTERN = "dd/MM/yyyy hh:mma";

    public static boolean isWithin24h(long timeStamp) {
        timeStamp = timeStamp * 1000;
        long now = System.currentTimeMillis();
        final long diff = now - timeStamp;
        return diff < 24 * HOUR_MILLIS;
    }

    public static String getFormattedDate(long timeStamp) {
        Date date = new java.util.Date(timeStamp * 1000);
        return new SimpleDateFormat(PATTERN).format(date);
    }

}
