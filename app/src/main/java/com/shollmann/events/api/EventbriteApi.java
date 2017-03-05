package com.shollmann.events.api;

import com.shollmann.events.api.baseapi.BaseApi;
import com.shollmann.events.api.baseapi.BaseApiCall;
import com.shollmann.events.api.baseapi.CachePolicy;
import com.shollmann.events.api.baseapi.CallId;
import com.shollmann.events.api.contract.EventbriteApiContract;
import com.shollmann.events.api.model.PaginatedEvents;
import com.shollmann.events.helper.Constants;

import retrofit.Callback;
import retrofit.RequestInterceptor;

public class EventbriteApi extends BaseApi<EventbriteApiContract> {

    public EventbriteApi(String baseUrl) {
        super(baseUrl, EventbriteApiContract.class);
    }

    @Override
    protected void onRequest(RequestInterceptor.RequestFacade request) {
        request.addQueryParam("token", Constants.EventbriteApi.TOKEN);
    }

    public void getEvents(String query, double lat, double lon, PaginatedEvents lastPageLoaded, CallId callId, Callback<PaginatedEvents> callback) {
        int pageNumber = lastPageLoaded != null ? lastPageLoaded.getPagination().getPageNumber() + 1 : 1;
        CachePolicy cachePolicy = CachePolicy.CACHE_ELSE_NETWORK;
        cachePolicy.setCacheKey(String.format("get_events_%1$s_%2$s_%3$s_%4$s", query, lat, lon, pageNumber));
        cachePolicy.setCacheTTL(Constants.Time.TEN_MINUTES);

        BaseApiCall<PaginatedEvents> apiCall = registerCall(callId, cachePolicy, callback, PaginatedEvents.class);

        if (apiCall != null && apiCall.requiresNetworkCall()) {
            getService().getEvents(query, lat, lon, pageNumber, apiCall);
        }
    }

}
