package com.shollmann.events.ui;

import android.app.Application;

import com.shollmann.events.api.EventbriteApi;
import com.shollmann.events.api.contract.EventbriteApiContract;
import com.shollmann.events.db.CachingDbHelper;
import com.shollmann.events.helper.Constants;

public class EventbriteApplication extends Application {
    private static EventbriteApplication instance;
    private CachingDbHelper cachingDbHelper;
    private EventbriteApi apiEventbrite;

    public static EventbriteApplication getApplication() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.instance = this;
        this.apiEventbrite = (EventbriteApi) new EventbriteApi.Builder()
                .baseUrl(Constants.EventbriteApi.URL)
                .contract(EventbriteApiContract.class)
                .build();
        this.cachingDbHelper = new CachingDbHelper(getApplicationContext());
    }

    public CachingDbHelper getCachingDbHelper() {
        return cachingDbHelper;
    }

    public EventbriteApi getApiEventbrite() {
        return apiEventbrite;
    }
}
