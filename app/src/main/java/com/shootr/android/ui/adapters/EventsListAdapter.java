package com.shootr.android.ui.adapters;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.util.PicassoWrapper;
import java.util.ArrayList;
import java.util.List;

public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ViewHolder> {

    private final PicassoWrapper picasso;
    private final Resources resources;

    private List<EventResultModel> events;
    private String currentVisibleCheckedInEvent;
    private String currentVisibleWathingEvent;

    private OnEventClickListener onEventClickListener;

    public EventsListAdapter(PicassoWrapper picasso, Resources resources) {
        this.picasso = picasso;
        this.resources = resources;
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

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_event, parent, false);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        EventResultModel event = events.get(position);
        holder.title.setText(event.getEventModel().getTitle());
        holder.date.setText(event.getEventModel().getDatetime());
        int watchers = event.getWatchers();
        if (watchers > 0) {
            holder.watchers.setVisibility(View.VISIBLE);
            holder.watchers.setText(getWatchersText(watchers));
        } else {
            holder.watchers.setVisibility(View.GONE);
        }
        holder.author.setText(event.getEventModel().getAuthorUsername());

        //TODO usar tamaño predefinido con picasso para mejorar rendimiento
        String pictureUrl = event.getEventModel().getPicture();
        picasso.loadEventPicture(pictureUrl).into(holder.picture);
        markEvents(holder, event);
    }

    private void markEvents(ViewHolder holder, EventResultModel event) {
        String idEvent = event.getEventModel().getIdEvent();
        boolean isSelectedEvent = idEvent.equals(currentVisibleCheckedInEvent);
        boolean isWatchingEvent = idEvent.equals(currentVisibleWathingEvent);
        if(isSelectedEvent && !isWatchingEvent){
            markSelectedEvent(holder, isSelectedEvent);
        }else if(isWatchingEvent && !isSelectedEvent){
            markWatchingEvent(holder, isWatchingEvent);
        }else if(isSelectedEvent && isWatchingEvent){
            markSelectedEvent(holder, isSelectedEvent);
        }else if(!isWatchingEvent && !isSelectedEvent){
            markSelectedEventAsRegularEvent(holder);
        }
    }

    private void markSelectedEventAsRegularEvent(ViewHolder holder) {
        holder.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    private void markSelectedEvent(ViewHolder holder, boolean isSelectedEvent) {
        if (isSelectedEvent) {
            holder.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_notifications_on_16_grey70, 0);
        } else {
            holder.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }
    private void markWatchingEvent(ViewHolder holder, boolean isWatchingEvent) {
        if (isWatchingEvent) {
            CharSequence text = holder.title.getText();
            SpannableStringBuilder sp = new SpannableStringBuilder(text);
            int selectedColor = resources.getColor(R.color.primary);
            sp.setSpan(new ForegroundColorSpan(selectedColor), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.title.setText(sp);
            holder.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_notifications_on_16_grey70, 0);
        } else {
            holder.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    private String getWatchersText(int watchers) {
        return String.valueOf(watchers);
    }

    @Override public int getItemCount() {
        return events.size();
    }

    public void setCurrentVisibleCheckedInEvent(String eventId) {
        this.currentVisibleCheckedInEvent = eventId;
    }

    public void setCurrentVisibleWatchingEvent(String eventId) {
        this.currentVisibleWathingEvent = eventId;
    }

    public void setOnEventClickListener(OnEventClickListener onEventClickListener) {
        this.onEventClickListener = onEventClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.event_picture) ImageView picture;
        @InjectView(R.id.event_title) TextView title;
        @InjectView(R.id.event_author) TextView author;
        @InjectView(R.id.event_date) TextView date;
        @InjectView(R.id.event_watchers) TextView watchers;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    EventResultModel eventSelected = events.get(ViewHolder.this.getPosition());
                    onEventClickListener.onEventClick(eventSelected.getEventModel());
                }
            });
        }
    }

    public interface OnEventClickListener {

        void onEventClick(EventModel event);
    }
}
