package com.shollmann.events.api.contract;

import com.shollmann.events.api.model.PaginatedEvents;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EventbriteApiContract {
    @GET("/v3/events/search/")
    Call<PaginatedEvents> getEvents(
            @Query("q") String query,
            @Query("location.latitude") double latitude,
            @Query("location.longitude") double longitude,
            @Query("page") int pageNumber);

}
