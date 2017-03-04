package com.shollmann.events.ui;

import android.app.Application;

import com.shollmann.events.api.EventbriteApi;
import com.shollmann.events.db.CachingDbHelper;
import com.shollmann.events.helper.Constants;

public class EventbriteApplication extends Application {
    private static EventbriteApplication instance;
    private CachingDbHelper cachingDbHelper;
    private EventbriteApi eventbriteApi;

    @Override
    public void onCreate() {
        super.onCreate();
        this.instance = this;
        this.eventbriteApi = new EventbriteApi(Constants.EventbriteApi.URL);
        this.cachingDbHelper = new CachingDbHelper(getApplicationContext());
    }

    public static EventbriteApplication getApplication() {
        return instance;
    }

    public CachingDbHelper getCachingDbHelper() {
        return cachingDbHelper;
    }

    public EventbriteApi getEventbriteApi() {
        return eventbriteApi;
    }
}
