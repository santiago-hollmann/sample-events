package com.shollmann.events.api.model;

import java.util.List;

public class PaginatedEvents {
    private Pagination pagination;
    private List<Event> events;

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
