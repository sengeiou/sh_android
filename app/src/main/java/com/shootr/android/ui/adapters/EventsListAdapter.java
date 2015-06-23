package com.shootr.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnEventClickListener;
import com.shootr.android.ui.adapters.listeners.OnUnwatchClickListener;
import com.shootr.android.ui.adapters.recyclerview.SubheaderRecyclerViewAdapter;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.util.PicassoWrapper;
import java.util.List;

public class EventsListAdapter extends SubheaderRecyclerViewAdapter<RecyclerView.ViewHolder, EventResultModel, EventResultModel> {

    private final PicassoWrapper picasso;

    private String currentCheckedInEvent;
    private OnEventClickListener onEventClickListener;
    private OnUnwatchClickListener onUnwatchClickListener;

    public EventsListAdapter(PicassoWrapper picasso, OnEventClickListener onEventClickListener) {
        this.picasso = picasso;
        this.onEventClickListener = onEventClickListener;
    }

    public void setEvents(List<EventResultModel> events) {
        boolean wasEmpty = getItems().isEmpty();
        setItems(events);
        if (wasEmpty) {
            notifyItemRangeInserted(0, events.size());
        } else {
            notifyDataSetChanged();
        }
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_event, parent, false);
        return new EventResultViewHolder(view, onEventClickListener, picasso);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateSubheaderViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_card_separator,
          parent,
          false);
        return new SubheaderViewHolder(view);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_event, parent, false);
        return new EventResultViewHolder(view, onEventClickListener, picasso);
    }

    @Override
    protected void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        EventResultModel event = getHeader();
        String idEvent = event.getEventModel().getIdEvent();

        WatchingEventResultViewHolder watchingEventResultViewHolder =
          new WatchingEventResultViewHolder(viewHolder.itemView, onEventClickListener, picasso, onUnwatchClickListener);

        boolean isCheckedInEvent = idEvent.equals(currentCheckedInEvent);
        watchingEventResultViewHolder.render(event, isCheckedInEvent);
    }

    @Override
    protected void onBindSubheaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        /* no-op */
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        EventResultModel event = getItem(position);
        String idEvent = event.getEventModel().getIdEvent();
        boolean isCheckedInEvent = idEvent.equals(currentCheckedInEvent);
        ((EventResultViewHolder) viewHolder).render(event, isCheckedInEvent);
    }

    public void setCurrentCheckedInEvent(String eventId) {
        this.currentCheckedInEvent = eventId;
    }

    public void setCurrentWatchingEvent(EventResultModel event) {
        this.setHeader(event);
    }

    public void setOnUnwatchClickListener(OnUnwatchClickListener onUnwatchClickListener) {
        this.onUnwatchClickListener = onUnwatchClickListener;
    }
}
