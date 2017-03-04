package com.shollmann.events.helper;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.shollmann.events.ui.EventbriteApplication;

public class PreferencesHelper {

    private final static Gson gson = new Gson();
    private static final String LAST_SEARCH = "last_search";

    static {
        prefs = PreferenceManager.getDefaultSharedPreferences(EventbriteApplication.getApplication());
    }


    private static SharedPreferences prefs;

    public static void clear() {
        prefs.edit().clear().commit();
    }

    public static int get(String key, int _default) {
        return prefs.getInt(key, _default);
    }

    public static String get(String key, String _default) {
        return prefs.getString(key, _default);
    }

    public static float get(String key, float _default) {
        return prefs.getFloat(key, _default);
    }

    public static boolean get(String key, boolean _default) {
        return prefs.getBoolean(key, _default);
    }

    public static long get(String key, long _default) {
        return prefs.getLong(key, _default);
    }

    public static void set(String key, long value) {
        prefs.edit().putLong(key, value).commit();
    }

    public static void set(String key, int value) {
        prefs.edit().putInt(key, value).commit();
    }

    public static void set(String key, String value) {
        prefs.edit().putString(key, value).commit();
    }

    public static void set(String key, float value) {
        prefs.edit().putFloat(key, value).commit();
    }

    public static void set(String key, boolean value) {
        prefs.edit().putBoolean(key, value).commit();
    }

    public static void remove(String key) {
        prefs.edit().remove(key).commit();
    }

    public static void setLastSearch(String lastSearch) {
        set(LAST_SEARCH, lastSearch);
    }

    public static String getLastSearch() {
        return get(LAST_SEARCH, Constants.EMPTY_STRING);
    }
}
