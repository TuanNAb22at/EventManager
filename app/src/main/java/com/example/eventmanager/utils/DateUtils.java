package com.example.eventmanager.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private static final SimpleDateFormat timeFormatter = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
    private static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());

    public static String formatDate(Date date) {
        return dateFormatter.format(date);
    }

    public static String formatTime(Date date) {
        return timeFormatter.format(date);
    }

    public static String formatDateTime(Date date) {
        return dateTimeFormatter.format(date);
    }

    public static Date parseDate(String dateString) {
        try {
            return dateFormatter.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parseTime(String timeString) {
        try {
            return timeFormatter.parse(timeString);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parseDateTime(String dateTimeString) {
        try {
            return dateTimeFormatter.parse(dateTimeString);
        } catch (ParseException e) {
            return null;
        }
    }
}

