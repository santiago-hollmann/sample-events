package com.shollmann.events.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shollmann.events.R;
import com.shollmann.events.api.model.Event;
import com.shollmann.events.ui.viewholder.EventViewHolder;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {
    private List<Event> listEvents;

    public EventAdapter(List<Event> listEvents) {
        this.listEvents = listEvents;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_event, parent, false);
        EventViewHolder eventViewHolder = new EventViewHolder(view);
        return eventViewHolder;
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        holder.setEvent(listEvents.get(position));
    }

    @Override
    public int getItemCount() {
        return listEvents.size();
    }

}
