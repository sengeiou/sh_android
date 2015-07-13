package com.shootr.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnEventClickListener;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.util.PicassoWrapper;
import java.util.Collections;
import java.util.List;

public class FavoriteEventsAdapter extends RecyclerView.Adapter<EventResultViewHolder> {

    private final PicassoWrapper picasso;

    private OnEventClickListener onEventClickListener;
    private List<EventResultModel> events = Collections.EMPTY_LIST;

    public FavoriteEventsAdapter(PicassoWrapper picasso, OnEventClickListener onEventClickListener) {
        this.picasso = picasso;
        this.onEventClickListener = onEventClickListener;
    }

    public void setEvents(List<EventResultModel> events) {
        boolean wasEmpty = this.events.isEmpty();
        this.events = events;
        if (wasEmpty) {
            notifyItemRangeInserted(0, events.size());
        } else {
            notifyDataSetChanged();
        }
    }

    @Override
    public EventResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_event, parent, false);
        return new EventResultViewHolder(view, onEventClickListener, picasso);
    }

    @Override
    public void onBindViewHolder(EventResultViewHolder holder, int position) {
        EventResultModel event = events.get(position);
        holder.render(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
