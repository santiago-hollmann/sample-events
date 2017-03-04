package com.shollmann.events.api.baseapi;

public enum CachePolicy {

    ///Never use the cache; always make a network call
    NETWORK_ONLY,

    ///First tries to load from the cache, but if that fails, it loads results from the network.
    CACHE_ELSE_NETWORK,

    ///If cache is expired and network call fails; answer the cache EVEN IF IT'S EXPIRED
    ///This is useful when you need the maximum possible availability; e.g. in the home activity
    CACHE_ELSE_NETWORK_ELSE_ANY_CACHE,

    ///Always answer from cache, EVEN IF IT'S EXPIRED; and if cache is expired refresh it in background
    ///This is useful when you need the maximum possible speed; e.g. in the home activity
    ANY_CACHE_THEN_NETWORK,

    ///Always use network call, but if that fails, then check if any cache is available and return it
    NETWORK_ELSE_ANY_CACHE;

    private long cacheTTL;
    private String cacheKey;

    public long getCacheTTL() {
        return cacheTTL;
    }

    public void setCacheTTL(long cacheTTL) {
        this.cacheTTL = cacheTTL;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }
}