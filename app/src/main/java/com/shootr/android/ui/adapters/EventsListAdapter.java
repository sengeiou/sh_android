package com.shootr.android.ui.adapters;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.util.PicassoWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ViewHolder> {

    private final PicassoWrapper picasso;
    private final Resources resources;

    private List<EventResultModel> events;

    public EventsListAdapter(PicassoWrapper picasso, Resources resources) {
        this.picasso = picasso;
        this.resources = resources;
        events = new ArrayList<>(0);
    }

    public void setEvents(List<EventResultModel> events) {
        this.events = events;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_event, parent, false);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        EventResultModel event = events.get(position);
        holder.title.setText(event.getEventModel().getTitle());
        holder.date.setText(event.getEventModel().getDatetime());
        int watchers = event.getWatchers();
        holder.watchers.setText(getWatchersText(watchers));
        //TODO usar tamaño predefinido con picasso para mejorar rendimiento
        String pictureUrl = event.getEventModel().getPicture();
        picasso.loadEventPicture(pictureUrl).into(holder.picture);
    }

    private String getWatchersText(int watchers) {
        return resources.getQuantityString(R.plurals.event_watchers, watchers, watchers);
    }

    @Override public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.event_picture) ImageView picture;
        @InjectView(R.id.event_title) TextView title;
        @InjectView(R.id.event_date) TextView date;
        @InjectView(R.id.event_watchers) TextView watchers;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
