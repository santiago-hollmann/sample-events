package com.shollmann.events.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shollmann.events.R;
import com.shollmann.events.api.model.Event;
import com.shollmann.events.ui.EventbriteApplication;
import com.squareup.picasso.Picasso;

public class EventViewHolder extends RecyclerView.ViewHolder {
    private TextView txtTitle;
    private TextView txtDate;
    private TextView txtIsFree;
    private ImageView imgCover;

    public EventViewHolder(View view) {
        super(view);
        txtTitle = (TextView) view.findViewById(R.id.event_txt_title);
        txtDate = (TextView) view.findViewById(R.id.event_txt_date);
        txtIsFree = (TextView) view.findViewById(R.id.event_txt_address);
        txtTitle = (TextView) view.findViewById(R.id.event_txt_title);
        imgCover = (ImageView) view.findViewById(R.id.event_img_cover);
        return;
    }

    public void setEvent(Event event) {
        txtTitle.setText(event.getName().getText());
        txtDate.setText(event.getStart().getLocal());
        txtIsFree.setText(event.getIsFree() ? R.string.free : R.string.paid);
        if (event.getLogo() != null) {
            Picasso.with(EventbriteApplication.getApplication()).load(event.getLogo().getUrl()).into(imgCover);
        }
    }

}
