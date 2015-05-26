package com.shootr.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.karumi.headerrecyclerview.HeaderRecyclerViewAdapter;
import com.shootr.android.R;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.util.PicassoWrapper;
import java.util.ArrayList;
import java.util.List;

public class EventsListAdapter extends HeaderRecyclerViewAdapter<RecyclerView.ViewHolder, EventModel, EventModel> {

    private final PicassoWrapper picasso;

    private List<EventResultModel> events;
    private String currentCheckedInEvent;
    private String currentWathingEvent;

    private OnEventClickListener onEventClickListener;

    public EventsListAdapter(PicassoWrapper picasso) {
        this.picasso = picasso;
        events = new ArrayList<>(0);
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
    protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup, int i) {
        return null; //TODO
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_event, viewGroup, false);
        return new EventResultViewHolder(view, onEventClickListener, picasso);
    }

    @Override
    protected void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        //TODO
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        EventResultModel event = events.get(position);
        String idEvent = event.getEventModel().getIdEvent();
        boolean isWatchingEvent = idEvent.equals(currentWathingEvent);
        boolean isCheckedInEvent = idEvent.equals(currentCheckedInEvent);
        ((EventResultViewHolder) viewHolder).render(event, isWatchingEvent, isCheckedInEvent);
    }



    @Override public int getItemCount() {
        return events.size();
    }

    public void setCurrentCheckedInEvent(String eventId) {
        this.currentCheckedInEvent = eventId;
    }

    public void setCurrentWatchingEvent(String eventId) {
        this.currentWathingEvent = eventId;
    }

    public void setOnEventClickListener(OnEventClickListener onEventClickListener) {
        this.onEventClickListener = onEventClickListener;
    }

    public interface OnEventClickListener {

        void onEventClick(EventModel event);
    }
}
