package com.shollmann.events.api.baseapi;

import android.text.TextUtils;

public class Cache {
    private long ttl;
    private String key;
    private Policy policy;

    private Cache(Builder builder) {
        this.policy = builder.cachePolicy;
        this.ttl = builder.cacheTTL;
        this.key = builder.cacheKey;
    }

    public long ttl() {
        return ttl;
    }

    public String key() {
        return key;
    }

    public Policy policy() {
        return policy;
    }

    public enum Policy {
        ///Never use the cache; always make a network call
        NETWORK_ONLY,

        ///First tries to load from the cache, but if that fails, it loads results from the network.
        CACHE_ELSE_NETWORK,

        ///If cache is expired and network call fails; answer the cache EVEN IF IT'S EXPIRED
        ///This is useful when you need the maximum possible availability
        CACHE_ELSE_NETWORK_ELSE_ANY_CACHE,

        ///Always answer from cache, EVEN IF IT'S EXPIRED; and if cache is expired refresh it in background
        ///This is useful when you need the maximum possible speed
        ANY_CACHE_THEN_NETWORK,

        ///Always use network call, but if that fails, then check if any cache is available and return it
        NETWORK_ELSE_ANY_CACHE;
    }

    public static final class Builder {
        private long cacheTTL;
        private String cacheKey;
        private Policy cachePolicy;

        public Builder ttl(long ttl) {
            cacheTTL = ttl;
            return this;
        }

        public Builder key(String key) {
            cacheKey = key;
            return this;
        }

        public Builder policy(Policy policy) {
            cachePolicy = policy;
            return this;
        }

        public Cache build() {
            if (TextUtils.isEmpty(cacheKey)) {
                throw new IllegalStateException("Cache key required!");
            }

            if (cacheTTL == 0) {
                cacheTTL = Time.ONE_MINUTE;
            }

            if (cachePolicy == null) {
                cachePolicy = Policy.CACHE_ELSE_NETWORK;
            }

            return new Cache(this);
        }
    }

    public class Time {
        public static final long TEN_SECONDS = 10;
        public static final long ONE_MINUTE = 60;
        public static final long TEN_MINUTES = 10 * ONE_MINUTE;
        public static final long ONE_HOUR = 6 * TEN_MINUTES;
    }
}