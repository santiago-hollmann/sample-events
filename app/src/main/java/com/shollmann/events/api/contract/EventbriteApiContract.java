package com.shollmann.events.api.contract;

import com.shollmann.events.api.model.PaginatedEvents;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface EventbriteApiContract {
    @GET("/v3/events/search/")
    void getEvents(
            @Query("q") String query,
            @Query("location.latitude") double latitude,
            @Query("location.longitude") double longitude,
            @Query("page") int pageNumber,
            Callback<PaginatedEvents> callback);

}
