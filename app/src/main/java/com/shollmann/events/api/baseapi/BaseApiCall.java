package com.shollmann.events.api.baseapi;

import com.shollmann.events.db.CachingDbHelper;
import com.shollmann.events.db.DbItem;
import com.shollmann.events.ui.EventbriteApplication;

import java.io.Serializable;
import java.lang.reflect.Type;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BaseApiCall<T> implements Callback<T> {

    private CachingDbHelper cachingDb;
    BaseApi baseApi;
    private CallId callId;
    private CachePolicy cachePolicy;
    private Callback<T> callback;
    Type responseType;

    private boolean isCancelled = false;

    private T pendingResult = null;
    private Response pendingResponse = null;
    private RetrofitError pendingError = null;

    public BaseApiCall(BaseApi baseApi, CallId callId, CachePolicy cachePolicy, Callback<T> callback, Type responseType) {
        this.cachingDb = EventbriteApplication.getApplication().getCachingDbHelper();
        this.baseApi = baseApi;
        this.callId = callId;
        this.cachePolicy = cachePolicy;
        this.callback = callback;
        this.responseType = responseType;
    }

    public boolean requiresNetworkCall() {
        if (cachePolicy == CachePolicy.NETWORK_ONLY || cachePolicy == CachePolicy.NETWORK_ELSE_ANY_CACHE) {
            return true;
        }
        DbItem<T> cachedResponse = cachingDb.getDbItem(cachePolicy.getCacheKey(), responseType);
        if (cachedResponse == null) {
            return true;
        }
        if (!cachedResponse.isExpired(cachePolicy.getCacheTTL())) {
            success(cachedResponse.getObject(), null);
            return false;
        } else if (cachePolicy == CachePolicy.ANY_CACHE_THEN_NETWORK) {
            success(cachedResponse.getObject(), null);
            if (cachePolicy != CachePolicy.ANY_CACHE_THEN_NETWORK) {
                return false;
            }
        }
        return true;
    }

    @Override
    public synchronized void success(T result, Response response) {
        if (response != null) { //response != null means that the result is from net and not from cache
            if (cachePolicy.getCacheKey() != null && result instanceof Serializable) {
                cachingDb.insert(cachePolicy.getCacheKey(), (Serializable) result, cachePolicy.getCacheTTL());
            } else {
            }
        }
        if (!isCancelled) {
            if (callback != null) {
                callback.success(result, response);
                baseApi.removeCall(callId);
            } else {
                pendingResult = result;
                pendingResponse = response;
            }
        } else {
        }
    }

    @Override
    public synchronized void failure(RetrofitError error) {
        if (!isCancelled) {
            if (callback != null) {
                if (cachePolicy == CachePolicy.CACHE_ELSE_NETWORK_ELSE_ANY_CACHE || cachePolicy == CachePolicy.NETWORK_ELSE_ANY_CACHE) {
                    DbItem<T> cachedResponse = cachingDb.getDbItem(cachePolicy.getCacheKey(), responseType);
                    if (cachedResponse == null) {
                        callback.failure(error);
                    } else {
                        callback.success(cachedResponse.getObject(), null);
                    }
                } else {
                    callback.failure(error);
                }
                baseApi.removeCall(callId);
            } else {
                pendingError = error;
            }
        } else {
        }
    }

    public synchronized void cancelCall() {
        removeCallback();
        isCancelled = true;
        baseApi.removeCall(callId);
    }

    public synchronized void removeCallback() {
        this.callback = null;
    }

    public synchronized void updateCallback(Callback<T> callback) {
        this.callback = callback;
        if (!isCancelled && callback != null) {
            if (pendingResponse != null) {
                success(pendingResult, pendingResponse);
            } else if (pendingError != null) {
                failure(pendingError);
            }
        }
    }

}
