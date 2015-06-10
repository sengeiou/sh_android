package com.shootr.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.util.PicassoWrapper;

public class EventHolder extends RecyclerView.ViewHolder {

    private final EventsAdapter.OnEventClickListener onEventClickListener;
    private final PicassoWrapper picasso;

    @InjectView(R.id.event_picture) ImageView picture;
    @InjectView(R.id.event_title) TextView title;
    @InjectView(R.id.event_author) TextView author;
    @InjectView(R.id.event_watchers) TextView watchers;

    public EventHolder(View itemView, EventsAdapter.OnEventClickListener onEventClickListener, PicassoWrapper picasso) {
        super(itemView);
        this.onEventClickListener = onEventClickListener;
        this.picasso = picasso;
        ButterKnife.inject(this, itemView);
    }

    public void render(EventModel event) {
        this.setClickListener(event);
        title.setText(event.getTitle());
        watchers.setVisibility(View.GONE);
        author.setText(event.getAuthorUsername());

        String pictureUrl = event.getPicture();
        picasso.loadEventPicture(pictureUrl).into(picture);
    }

    private void setClickListener(final EventModel eventResult) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onEventClickListener.onEventClick(eventResult);
            }
        });
    }
}
