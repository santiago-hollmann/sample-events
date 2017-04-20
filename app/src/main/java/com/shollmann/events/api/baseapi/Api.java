package com.shollmann.events.api.baseapi;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.shollmann.events.helper.Constants;
import com.shollmann.events.ui.EventbriteApplication;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Api<T> {

    private static final long DEFAULT_TIMEOUT = 10;
    private static final long DEFAULT_CACHE_DIR_SIZE = Constants.Size.TWO_MEBIBYTES;
    private T service;
    private Map<CallId, BaseApiCall> ongoingCalls = new HashMap<>();

    public Api(Builder builder) {
        service = buildApiService((T) builder.contract, builder.baseUrl, builder.timeOut, builder.cacheSize);
    }

    private okhttp3.Cache createHttpCache(long dirSize) {
        String cacheDirectoryName = this.getClass().getSimpleName() + Constants.CACHE;
        File cacheDirectory = new File(EventbriteApplication.getApplication().getCacheDir(), cacheDirectoryName);
        return new okhttp3.Cache(cacheDirectory, dirSize);
    }

    private T buildApiService(T contract, String baseUrl, long timeOut, long cacheSize) {
        HttpLoggingInterceptor interceptorLogging = new HttpLoggingInterceptor();
        interceptorLogging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(timeOut, TimeUnit.SECONDS)
                .readTimeout(timeOut, TimeUnit.SECONDS)
                .writeTimeout(timeOut, TimeUnit.SECONDS)
                .addInterceptor(interceptorLogging)
                .addInterceptor(generateDefaultInterceptor())
                .cache(createHttpCache(cacheSize))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create((Class<T>) contract);
    }

    @NonNull
    private Interceptor generateDefaultInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("token", Constants.EventbriteApi.TOKEN)
                        .build();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .url(url);

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };
    }

    protected T getService() {
        return service;
    }

    protected synchronized <CT> BaseApiCall<CT> registerCall(CallId callId, Cache cachePolicy, Callback<CT> callback, Type responseType) {
        if (ongoingCalls.containsKey(callId)) {
            cancelCall(callId);
        }
        BaseApiCall<CT> newCall = new BaseApiCall<>(this, callId, cachePolicy, callback, responseType);
        if (callback == null) {
            newCall.cancelCall(); // If callback == null on register then ignore the response.
        }
        ongoingCalls.put(callId, newCall);
        return newCall;
    }

    private synchronized void cancelCall(CallId callId) {
        BaseApiCall ongoingCall = ongoingCalls.get(callId);
        if (ongoingCall != null) {
            ongoingCall.cancelCall();
            ongoingCalls.remove(callId);
        }
    }

    synchronized void removeCall(CallId callId) {
        BaseApiCall ongoingCall = ongoingCalls.get(callId);
        if (ongoingCall != null) {
            ongoingCalls.remove(callId);
        }
    }

    public synchronized boolean registerCallback(CallId callId, Callback callback) {
        BaseApiCall ongoingCall = ongoingCalls.get(callId);
        if (ongoingCall != null) {
            ongoingCall.updateCallback(callback);
            return true;
        } else {
            return false;
        }
    }

    public synchronized boolean unregisterCallback(CallId callId) {
        BaseApiCall ongoingCall = ongoingCalls.get(callId);
        if (ongoingCall != null) {
            ongoingCall.removeCallback();
            return true;
        } else {
            return false;
        }
    }

    public abstract static class Builder<T extends Builder, A extends Api, C> {
        private String baseUrl;
        private C contract;
        private long timeOut;
        private long cacheSize;

        public T baseUrl(String url) {
            baseUrl = url;
            return (T) this;
        }

        public T contract(C aContract) {
            contract = aContract;
            return (T) this;
        }

        public T timeout(long timeout) {
            timeOut = timeout;
            return (T) this;
        }

        public T cacheSize(long size) {
            cacheSize = size;
            return (T) this;
        }

        public abstract A build();


        public void validate() {
            if (TextUtils.isEmpty(baseUrl)) {
                throw new IllegalStateException("Base Url required!");
            }

            if (contract == null) {
                throw new IllegalStateException("Contract required!");
            }

            if (cacheSize == 0) {
                cacheSize = DEFAULT_CACHE_DIR_SIZE;
            }

            if (timeOut == 0) {
                timeOut = DEFAULT_TIMEOUT;
            }
        }

    }
}