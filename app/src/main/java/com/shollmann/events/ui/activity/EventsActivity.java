package com.shollmann.events.ui.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.shollmann.events.api.model.PaginatedEvents;
import com.shollmann.events.helper.Constants;
import com.shollmann.events.helper.PreferencesHelper;
import com.shollmann.events.helper.ResourcesHelper;
import com.shollmann.events.ui.EventbriteApplication;
import com.shollmann.events.ui.adapter.EventAdapter;

import java.text.DecimalFormat;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EventsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final int NO_FLAGS = 0;
    private static final int ACCESS_COARSE_LOCATION_PERMISSION_REQUEST = 7001;

    private Toolbar toolbar;
    private TextView txtNoResults;
    private TextView txtWaitForResults;
    private SearchView searchView;
    private MenuItem menuSearch;
    private CoordinatorLayout coordinatorLayout;
    private EventbriteApi eventbriteApi;
    private Location location;
    private RecyclerView recyclerEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        eventbriteApi = EventbriteApplication.getApplication().getEventbriteApi();

        findViews();
        setupTaskDescription();
        setupToolbar();
        checkForLocationPermission();

    }

    private void setupRecyclerView(List<Event> eventList) {
        recyclerEvents.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerEvents.setLayoutManager(layoutManager);

        EventAdapter eventAdapter = new EventAdapter(eventList);
        recyclerEvents.setAdapter(eventAdapter);

        recyclerEvents.setVisibility(View.VISIBLE);
    }

    private void checkForLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
//            txtLoading.setClickable(false);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    ACCESS_COARSE_LOCATION_PERMISSION_REQUEST);

        } else {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            String lastSearch = PreferencesHelper.getLastSearch();
            getEvents(TextUtils.isEmpty(lastSearch) ? Constants.EMPTY_STRING : lastSearch);
        }
    }

    private void getEvents(String query) {
        Snackbar.make(coordinatorLayout, R.string.getting_events, Snackbar.LENGTH_SHORT).show();
        CallId getEventsCallId = new CallId(CallOrigin.HOME, CallType.GET_EVENTS);
//        eventbriteApi.getEvents(query, Double.valueOf(new DecimalFormat("##,###").format(location.getLatitude())), Double.valueOf(new DecimalFormat("###,###").format(location.getLongitude())), getEventsCallId, generateGetEventsCallback());
        eventbriteApi.getEvents(query, location.getLatitude(), location.getLongitude(), getEventsCallId, generateGetEventsCallback());
        PreferencesHelper.setLastSearch(query);
//        } else {
//            Snackbar.make(coordinatorLayout, R.string.please_enter_a_valid_search_term, Snackbar.LENGTH_SHORT).show();
//        }
    }

    private Callback<PaginatedEvents> generateGetEventsCallback() {
        return new Callback<PaginatedEvents>() {

            @Override
            public void success(PaginatedEvents paginatedEvents, Response response) {
                setupRecyclerView(paginatedEvents.getEvents());
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
        recyclerEvents = (RecyclerView) findViewById(R.id.home_events_recycler);
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
