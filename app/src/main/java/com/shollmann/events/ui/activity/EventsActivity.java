package com.shollmann.events.ui.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.shollmann.events.ui.event.LoadMoreEvents;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private EventAdapter eventAdapter;
    private PaginatedEvents lastPageLoaded;
    private String currentQuery;
    private CallId getEventsCallId;

    public static double getShorterCoordinate(double coordinate) {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat(Constants.COORDINATES_FORMAT, dfs);

        return Double.parseDouble(decimalFormat.format(coordinate));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        eventbriteApi = EventbriteApplication.getApplication().getApiEventbrite();

        findViews();
        setupTaskDescription();
        setupToolbar();
        setupRecyclerView();
        checkForLocationPermission();
    }

    private void setupRecyclerView() {
        recyclerEvents.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerEvents.setLayoutManager(layoutManager);
        eventAdapter = new EventAdapter(new ArrayList<Event>());
        recyclerEvents.setAdapter(eventAdapter);

        recyclerEvents.setVisibility(View.GONE);
    }

    private void updateEventsList(List<Event> eventList) {
        eventAdapter.add(eventList);
        eventAdapter.notifyDataSetChanged();

        if (recyclerEvents.getVisibility() != View.VISIBLE) {
            recyclerEvents.setVisibility(View.VISIBLE);
        }
    }

    private void checkForLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    ACCESS_COARSE_LOCATION_PERMISSION_REQUEST);

        } else {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            getEvents(null);
        }
    }


    private void getEvents(String query) {
        Snackbar.make(coordinatorLayout, R.string.getting_events, Snackbar.LENGTH_SHORT).show();
        getEventsCallId = new CallId(CallOrigin.HOME, CallType.GET_EVENTS);
        Callback<PaginatedEvents> callback = generateGetEventsCallback();
        eventbriteApi.registerCallback(getEventsCallId, callback);
        try {
            eventbriteApi.getEvents(query, getShorterCoordinate(37.773972), getShorterCoordinate(-122.431297
            ), lastPageLoaded, getEventsCallId, callback);
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
        }
        PreferencesHelper.setLastSearch(query);
    }

    private Callback<PaginatedEvents> generateGetEventsCallback() {
        return new Callback<PaginatedEvents>() {

            @Override
            public void onResponse(Call<PaginatedEvents> call, Response<PaginatedEvents> response) {
                PaginatedEvents paginatedEvents = response.body();
                if (paginatedEvents.getEvents().isEmpty()) {
                    eventAdapter.setKeepLoading(false);
                    if (eventAdapter.getItemCount() == 0) {
                        txtNoResults.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                txtNoResults.setVisibility(View.GONE);
                updateEventsList(paginatedEvents.getEvents());
                lastPageLoaded = paginatedEvents;
            }

            @Override
            public void onFailure(Call<PaginatedEvents> call, Throwable t) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap icon = BitmapFactory.decodeResource(ResourcesHelper.getResources(),
                    R.mipmap.ic_launcher);
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(ResourcesHelper.getString(R.string.app_name), icon, ResourcesHelper.getResources().getColor(R.color.colorPrimary));
            this.setTaskDescription(taskDescription);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        menuSearch = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(menuSearch);
        EditText edtSearch = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        ImageView searchClose = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        searchClose.setImageResource(R.drawable.ic_action_close);
        edtSearch.setTextColor(Color.WHITE);
        edtSearch.setHintTextColor(Color.WHITE);
        searchView.setOnQueryTextListener(this);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        resetSearch();
        currentQuery = query.trim();
        getEvents(currentQuery);
        searchView.setQuery(Constants.EMPTY_STRING, false);
        searchView.setIconified(true);
        hideKeyboard();
        return true;
    }

    private void resetSearch() {
        lastPageLoaded = null;
        eventAdapter.reset();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoadMoreEvents event) {
        getEvents(currentQuery);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        eventbriteApi.unregisterCallback(getEventsCallId);
        EventBus.getDefault().unregister(this);
    }
}
