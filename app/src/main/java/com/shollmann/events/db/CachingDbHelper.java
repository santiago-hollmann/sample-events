package com.shollmann.events.db;

import android.content.Context;

public class CachingDbHelper extends DbHelperTemplate {
    private final static int DB_VERSION = 4;
    private final static String DB_NAME = "eventsDatabase";
    private final static String TABLE_NAME = "cache";
    private final static String COLUMN_KEY = "cacheKey";
    private final static String COLUMN_DATA = "cacheData";
    private final static String COLUMN_DATE = "cacheDate";
    private final static boolean AUTO_PURGE = true;

    public CachingDbHelper(Context context) {
        super(context, DB_NAME, DB_VERSION, TABLE_NAME, COLUMN_KEY, COLUMN_DATA, COLUMN_DATE, AUTO_PURGE);
    }
}