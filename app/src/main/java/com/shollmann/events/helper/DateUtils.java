package com.shollmann.events.helper;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    private static final int MILLISECONDS_IN_A_DAY = 1000 * 60 * 60 * 24;
    private static final int MILLISECONDS_IN_A_MINUTE = 1000 * 60;

    private static long getMillisForDate(java.util.Date date) {
        return date.getTime();
    }

    public static int getDifferenceInDays(java.util.Date firstDate, java.util.Date secondDate) {
        return getDifferenceInDays(firstDate.getTime(), secondDate.getTime());
    }

    private static int getDifferenceInDays(long firstDate, long secondDate) {
        return Math.round((firstDate - secondDate) / MILLISECONDS_IN_A_DAY);
    }

    private static int getDifferenceInMinutes(long firstDate, long secondDate) {
        return Math.round((firstDate - secondDate) / MILLISECONDS_IN_A_MINUTE);
    }

    public static String getDateFormatted(java.util.Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static int diffSeconds(long d1, long d2) {
        long diff = Math.abs(d1 - d2);
        diff = diff / (1000);
        return (int) diff;
    }

    public static int diffSeconds(long d1) {
        return diffSeconds(System.currentTimeMillis(), d1);
    }

    public static String formatDateTimeToDDMMYYYY(java.util.Date date) {
        SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
        return dt.format(date.getTime());
    }

    public static String getCurrentMMYYYY() {
        SimpleDateFormat dt = new SimpleDateFormat("MM/yyyy");
        return dt.format(Calendar.getInstance().getTime());
    }

    public static String formatTimeSpan(long seconds) {

        final DecimalFormat f = new DecimalFormat("0.#");

        if (seconds >= 86400) { // 86400 = 60 * 60 * 24 = 1 day
            return f.format(seconds / 86400.) + "d";
        } else if (seconds >= 3600) { // 3600 = 60 * 60 = 1 hour
            return f.format(seconds / 3600.) + "h";
        } else if (seconds >= 60) { // 60 = 1 minute
            return f.format(seconds / 60.) + "m";
        } else {
            return seconds + "s";
        }
    }

    public static String getEventDate(String dateNaiveIso8601) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateNaiveIso8601);
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, ''yy");
            return formatter.format(date);
        } catch (ParseException e) {
            return Constants.EMPTY_STRING;
        }
    }
}
