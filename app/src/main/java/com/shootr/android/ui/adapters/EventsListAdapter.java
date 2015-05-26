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

    public static class EventResultViewHolder extends RecyclerView.ViewHolder {

        private final OnEventClickListener onEventClickListener;
        private final PicassoWrapper picasso;

        @InjectView(R.id.event_picture) ImageView picture;
        @InjectView(R.id.event_title) TextView title;
        @InjectView(R.id.event_author) TextView author;
        @InjectView(R.id.event_date) TextView date;
        @InjectView(R.id.event_watchers) TextView watchers;
        @InjectView(R.id.event_notification_indicator) View notificationIndicator;

        public EventResultViewHolder(View itemView, OnEventClickListener onEventClickListener, PicassoWrapper picasso) {
            super(itemView);
            this.onEventClickListener = onEventClickListener;
            this.picasso = picasso;
            ButterKnife.inject(this, itemView);
        }

        public void render(EventResultModel event, boolean isWatching, boolean isCheckedIn) {
            this.setClickListener(event);
            title.setText(event.getEventModel().getTitle());
            date.setText(event.getEventModel().getDatetime());
            int watchersCount = event.getWatchers();
            if (watchersCount > 0) {
                watchers.setVisibility(View.VISIBLE);
                watchers.setText(getWatchersText(watchersCount));
            } else {
                watchers.setVisibility(View.GONE);
            }
            author.setText(event.getEventModel().getAuthorUsername());

            //TODO usar tamaño predefinido con picasso para mejorar rendimiento
            String pictureUrl = event.getEventModel().getPicture();
            picasso.loadEventPicture(pictureUrl).into(picture);

            if (isCheckedIn) {
                setNotificationIconVisibility(true);
            } else {
                setNotificationIconVisibility(false);
            }

            if (isWatching) {
                setHighlightColorVisibility(true);
            } else {
                setHighlightColorVisibility(false);
            }
        }

        private void setClickListener(final EventResultModel eventResult) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    onEventClickListener.onEventClick(eventResult.getEventModel());
                }
            });
        }

        private String getWatchersText(int watchers) {
            return String.valueOf(watchers);
        }

        private void setNotificationIconVisibility(boolean visible) {
            notificationIndicator.setVisibility(visible? View.VISIBLE : View.GONE);
        }

        private void setHighlightColorVisibility(boolean showHighlight) {
            if (showHighlight) {
                CharSequence text = title.getText();
                SpannableStringBuilder sp = new SpannableStringBuilder(text);
                int selectedColor = itemView.getContext().getResources().getColor(R.color.primary);
                sp.setSpan(new ForegroundColorSpan(selectedColor), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                title.setText(sp);
            }
        }
    }

    public interface OnEventClickListener {

        void onEventClick(EventModel event);
    }
}
