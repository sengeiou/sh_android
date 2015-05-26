package com.shootr.android.ui.adapters;

import android.content.res.Resources;
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
import com.karumi.headerrecyclerview.HeaderRecyclerViewAdapter;
import com.shootr.android.R;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.util.PicassoWrapper;
import java.util.ArrayList;
import java.util.List;

public class EventsListAdapter extends HeaderRecyclerViewAdapter<RecyclerView.ViewHolder, EventModel, EventModel> {

    private final PicassoWrapper picasso;
    private final Resources resources;

    private List<EventResultModel> events;
    private String currentCheckedInEvent;
    private String currentWathingEvent;

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

    @Override
    protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup, int i) {
        return null; //TODO
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_event, viewGroup, false);
        return new EventViewHolder(view);
    }

    @Override
    protected void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        //TODO
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        EventViewHolder holder = (EventViewHolder) viewHolder;
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

    private void markEvents(EventViewHolder holder, EventResultModel event) {
        String idEvent = event.getEventModel().getIdEvent();
        boolean isCheckedInEvent = idEvent.equals(currentCheckedInEvent);
        boolean isWatchingEvent = idEvent.equals(currentWathingEvent);

        if (isCheckedInEvent) {
            setNotificationIconVisibility(holder, true);
        } else {
            setNotificationIconVisibility(holder, false);
        }

        if (isWatchingEvent) {
            setHighlightColorVisibility(holder, true);
        } else {
            setHighlightColorVisibility(holder, false);
        }
    }

    private void setNotificationIconVisibility(EventViewHolder holder, boolean visible) {
        holder.notificationIndicator.setVisibility(visible? View.VISIBLE : View.GONE);
    }

    private void setHighlightColorVisibility(EventViewHolder holder, boolean showHighlight) {
        if (showHighlight) {
            CharSequence text = holder.title.getText();
            SpannableStringBuilder sp = new SpannableStringBuilder(text);
            int selectedColor = resources.getColor(R.color.primary);
            sp.setSpan(new ForegroundColorSpan(selectedColor), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.title.setText(sp);
        }
    }

    private String getWatchersText(int watchers) {
        return String.valueOf(watchers);
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

    public class EventViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.event_picture) ImageView picture;
        @InjectView(R.id.event_title) TextView title;
        @InjectView(R.id.event_author) TextView author;
        @InjectView(R.id.event_date) TextView date;
        @InjectView(R.id.event_watchers) TextView watchers;
        @InjectView(R.id.event_notification_indicator) View notificationIndicator;

        public EventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    EventResultModel eventSelected = events.get(EventViewHolder.this.getPosition());
                    onEventClickListener.onEventClick(eventSelected.getEventModel());
                }
            });
        }
    }

    public interface OnEventClickListener {

        void onEventClick(EventModel event);
    }
}
