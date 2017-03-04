package com.shollmann.events.api.baseapi;

import android.text.TextUtils;

import com.shollmann.events.helper.Constants;


public class CallId implements Comparable<CallId> {

    private CallOrigin origin;
    private CallType type;
    private String params;

    public CallId(CallOrigin origin, CallType type) {
        this.origin = origin;
        this.type = type;

    }

    public CallId(CallOrigin origin, CallType type, String params) {
        this.origin = origin;
        this.type = type;
        this.params = params;
    }

    public CallOrigin getOrigin() {
        return origin;
    }

    public CallType getType() {
        return type;
    }

    @Override
    public int compareTo(CallId another) {
        final int EQUAL = 0;
        if (this.equals(another)) return EQUAL;

        int originComparison = this.origin.compareTo(another.origin);
        if (originComparison != EQUAL) return originComparison;

        int typeComparison = this.type.compareTo(another.type);
        if (typeComparison != EQUAL) return typeComparison;

        int paramsComparison = this.params.compareTo(another.params);
        if (paramsComparison != EQUAL) return paramsComparison;

        return EQUAL;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        CallId other = (CallId) obj;
        return this.origin == other.origin && this.type == other.type && TextUtils.equals(this.params, other.params);
    }

    @Override
    public int hashCode() {
        return origin.hashCode() + type.hashCode() + (params != null ? params.hashCode() : 0);
    }

    @Override
    public String toString() {
        return String.format("%s-%s%s", origin.name(), type.name(), (params != null ? "-" + params.toString() : Constants.EMPTY_STRING));
    }

}

