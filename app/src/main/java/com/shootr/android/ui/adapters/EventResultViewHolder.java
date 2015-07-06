package com.shootr.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnEventClickListener;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.util.PicassoWrapper;

public class EventResultViewHolder extends RecyclerView.ViewHolder {

    private final OnEventClickListener onEventClickListener;
    private final PicassoWrapper picasso;

    @Bind(R.id.event_picture) ImageView picture;
    @Bind(R.id.event_title) TextView title;
    @Bind(R.id.event_author) TextView author;
    @Bind(R.id.event_watchers) TextView watchers;
    @Bind(R.id.event_notification_indicator) View notificationIndicator;

    public EventResultViewHolder(View itemView,
      OnEventClickListener onEventClickListener,
      PicassoWrapper picasso) {
        super(itemView);
        this.onEventClickListener = onEventClickListener;
        this.picasso = picasso;
        ButterKnife.bind(this, itemView);
    }

    public void render(EventResultModel event, boolean isCheckedIn) {
        this.setClickListener(event);
        title.setText(event.getEventModel().getTitle());
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
    }

    private void setClickListener(final EventResultModel eventResult) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onEventClickListener.onEventClick(eventResult);
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
