package com.shollmann.events.db;


import com.shollmann.events.helper.DateUtils;

public class DbItem<T> {

    private T object;
    private long date;

    public DbItem(T object, long date) {
        this.object = object;
        this.date = date;
    }

    public T getObject() {
        return object;
    }

    public boolean isExpired(long ttl) {
        int seconds = DateUtils.diffSeconds(date);
        return seconds > ttl;
    }

}