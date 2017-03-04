package com.shollmann.events.ui.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.shollmann.events.R;
import com.shollmann.events.api.EventbriteApi;
import com.shollmann.events.api.baseapi.CallId;
import com.shollmann.events.api.baseapi.CallOrigin;
import com.shollmann.events.api.baseapi.CallType;
import com.shollmann.events.api.model.Event;
import com.shollmann.events.helper.Constants;
import com.shollmann.events.helper.PreferencesHelper;
import com.shollmann.events.helper.ResourcesHelper;
import com.shollmann.events.ui.EventbriteApplication;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EventsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final int NO_FLAGS = 0;

    private Toolbar toolbar;
    private TextView txtNoResults;
    private TextView txtWaitForResults;
    private SearchView searchView;
    private MenuItem menuSearch;
    private CoordinatorLayout coordinatorLayout;
    private EventbriteApi eventbriteApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        eventbriteApi = EventbriteApplication.getApplication().getEventbriteApi();

        findViews();
        setupTaskDescription();
        setupToolbar();

        String lastSearch = PreferencesHelper.getLastSearch();

        getEvents(TextUtils.isEmpty(lastSearch) ? Constants.EMPTY_STRING : lastSearch);
    }

    private void getEvents(String query) {
        if (!TextUtils.isEmpty(query)) {
            Snackbar.make(coordinatorLayout, R.string.getting_events, Snackbar.LENGTH_SHORT).show();
            CallId getEventCallId = new CallId(CallOrigin.HOME, CallType.GET_EVENTS);
//            eventbriteApi.getEvents(query, query, lat, lon, generateGetEventsCallback());
            PreferencesHelper.setLastSearch(query);
        } else {
            Snackbar.make(coordinatorLayout, R.string.please_enter_a_location, Snackbar.LENGTH_SHORT).show();
        }
    }

    private Callback<Event> generateGetEventsCallback() {
        return new Callback<Event>() {

            @Override
            public void success(Event event, Response response) {
            }

            @Override
            public void failure(RetrofitError error) {
                handleGetEventsFailure();
            }
        };
    }

    private void handleGetEventsFailure() {
        txtWaitForResults.setVisibility(View.GONE);
        txtNoResults.setVisibility(View.VISIBLE);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtNoResults = (TextView) findViewById(R.id.home_txt_no_results);
        txtWaitForResults = (TextView) findViewById(R.id.home_txt_wait_first_time);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.home_coordinator_layout);
    }

    private void setupTaskDescription() {
        Bitmap icon = BitmapFactory.decodeResource(ResourcesHelper.getResources(),
                R.mipmap.ic_launcher);
        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(ResourcesHelper.getString(R.string.app_name), icon, ResourcesHelper.getResources().getColor(R.color.colorPrimary));
        this.setTaskDescription(taskDescription);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        menuSearch = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(menuSearch);
        searchView.setOnQueryTextListener(this);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        getEvents(query.trim());
        searchView.setQuery(Constants.EMPTY_STRING, false);
        searchView.setIconified(true);
        hideKeyboard();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) EventbriteApplication.getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), NO_FLAGS);
        }
    }
}
