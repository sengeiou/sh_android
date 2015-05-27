package com.shootr.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.recyclerview.SubheaderRecyclerViewAdapter;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.util.PicassoWrapper;
import java.util.List;

public class EventsListAdapter extends SubheaderRecyclerViewAdapter<RecyclerView.ViewHolder, EventResultModel, EventResultModel> {

    private final PicassoWrapper picasso;

    private String currentCheckedInEvent;
    private OnEventClickListener onEventClickListener;

    public EventsListAdapter(PicassoWrapper picasso) {
        this.picasso = picasso;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_event,
          parent,
          false);
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

        EventResultViewHolder headerHolder = (EventResultViewHolder) viewHolder;
        boolean isCheckedInEvent = idEvent.equals(currentCheckedInEvent);
        headerHolder.render(event, isCheckedInEvent);
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

    public void setCurrentWatchingEvent(String eventId) {
        // FIXME metodo poco optimo. Estaria genial si recibiera el EventModel, pero eso implica cambiar el Presener de EventList y quiza el Interactor
        this.setHeader(getEventWithId(eventId));
    }

    private EventResultModel getEventWithId(String eventId) {
        if (eventId == null) {
            return null;
        }
        for (EventResultModel event : getItems()) {
            if (event.getEventModel().getIdEvent().equals(eventId)) {
                return event;
            }
        }
        return null;
    }

    public void setOnEventClickListener(OnEventClickListener onEventClickListener) {
        this.onEventClickListener = onEventClickListener;
    }

    public interface OnEventClickListener {

        void onEventClick(EventModel event);
    }
}
