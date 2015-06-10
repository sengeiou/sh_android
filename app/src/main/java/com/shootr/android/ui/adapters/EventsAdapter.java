package com.shootr.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.recyclerview.SubheaderRecyclerViewAdapter;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.util.PicassoWrapper;
import java.util.List;

public class EventsAdapter extends SubheaderRecyclerViewAdapter<RecyclerView.ViewHolder, EventModel, EventModel> {

    private final PicassoWrapper picasso;

    private OnEventClickListener onEventClickListener;

    public EventsAdapter(PicassoWrapper picasso) {
        this.picasso = picasso;
    }

    public void setEvents(List<EventModel> events) {
        boolean wasEmpty = getItems().isEmpty();
        setItems(events);
        if (wasEmpty) {
            notifyItemRangeInserted(0, events.size());
        } else {
            notifyDataSetChanged();
        }
    }

    @Override protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_event, parent, false);
        return new EventHolder(view, onEventClickListener, picasso);
    }

    @Override protected RecyclerView.ViewHolder onCreateSubheaderViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_card_separator,
          parent,
          false);
        return new SubheaderViewHolder(view);
    }

    @Override protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_event, parent, false);
        return new EventHolder(view, onEventClickListener, picasso);
    }

    @Override protected void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        /* no-op */
    }

    @Override protected void onBindSubheaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        /* no-op */
    }

    @Override protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        EventModel event = getItem(position);
        ((EventHolder) holder).render(event);
    }

    public void setOnEventClickListener(OnEventClickListener onEventClickListener) {
        this.onEventClickListener = onEventClickListener;
    }

    public interface OnEventClickListener {

        void onEventClick(EventModel event);
    }
}
