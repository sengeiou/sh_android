package com.shootr.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnEventClickListener;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.util.PicassoWrapper;

public class EventResultViewHolder extends RecyclerView.ViewHolder {

    private final OnEventClickListener onEventClickListener;
    private final PicassoWrapper picasso;

    @Bind(R.id.event_picture) ImageView picture;
    @Bind(R.id.stream_title) TextView title;
    @Bind(R.id.stream_author) TextView author;
    @Bind(R.id.event_watchers) TextView watchers;

    public EventResultViewHolder(View itemView,
      OnEventClickListener onEventClickListener,
      PicassoWrapper picasso) {
        super(itemView);
        this.onEventClickListener = onEventClickListener;
        this.picasso = picasso;
        ButterKnife.bind(this, itemView);
    }

    public void render(StreamResultModel event) {
        this.setClickListener(event);
        title.setText(event.getStreamModel().getTitle());
        int watchersCount = event.getWatchers();
        if (watchersCount > 0) {
            watchers.setVisibility(View.VISIBLE);
            watchers.setText(getWatchersText(watchersCount));
        } else {
            watchers.setVisibility(View.GONE);
        }
        author.setText(event.getStreamModel().getAuthorUsername());

        //TODO usar tamaño predefinido con picasso para mejorar rendimiento
        String pictureUrl = event.getStreamModel().getPicture();
        picasso.loadEventPicture(pictureUrl).into(picture);
    }

    private void setClickListener(final StreamResultModel eventResult) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onEventClickListener.onEventClick(eventResult);
            }
        });
    }

    private String getWatchersText(int watchers) {
        return String.valueOf(watchers);
    }
}
