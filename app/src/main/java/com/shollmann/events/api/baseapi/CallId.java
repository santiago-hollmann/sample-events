package com.shollmann.events.api.baseapi;

import android.support.annotation.NonNull;

public class CallId implements Comparable<CallId> {

    private CallOrigin origin;
    private CallType type;

    public CallId(CallOrigin origin, CallType type) {
        this.origin = origin;
        this.type = type;

    }

    @Override
    public int compareTo(@NonNull CallId another) {
        final int equal = 0;
        if (this.equals(another)) return equal;

        int originComparison = this.origin.compareTo(another.origin);
        if (originComparison != equal) return originComparison;

        int typeComparison = this.type.compareTo(another.type);
        if (typeComparison != equal) return typeComparison;

        return equal;
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
        return this.origin == other.origin && this.type == other.type;
    }

    @Override
    public int hashCode() {
        return origin.hashCode() + type.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s-%s", origin.name(), type.name());
    }

}

