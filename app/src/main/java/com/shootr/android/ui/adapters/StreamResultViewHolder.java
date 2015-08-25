package com.shootr.android.ui.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.util.ImageLoader;

public class StreamResultViewHolder extends RecyclerView.ViewHolder {

    private final OnStreamClickListener onStreamClickListener;
    private final ImageLoader imageLoader;

    private boolean showsWatchersText = false;

    @Bind(R.id.stream_picture) ImageView picture;
    @Bind(R.id.stream_title) TextView title;
    @Bind(R.id.stream_watchers) TextView watchers;
    @Bind(R.id.separator) View separator;
    @Nullable @Bind(R.id.stream_author) TextView author;

    public StreamResultViewHolder(View itemView, OnStreamClickListener onStreamClickListener,
      ImageLoader imageLoader) {
        super(itemView);
        this.onStreamClickListener = onStreamClickListener;
        this.imageLoader = imageLoader;
        ButterKnife.bind(this, itemView);
    }

    public void render(StreamResultModel streamResultModel, boolean showSeparator) {
        this.setClickListener(streamResultModel);
        title.setText(streamResultModel.getStreamModel().getTitle());
        int watchersCount = streamResultModel.getWatchers();
        if (watchersCount > 0) {
            watchers.setVisibility(View.VISIBLE);
            watchers.setText(getWatchersText(watchersCount));
        } else {
            watchers.setVisibility(View.GONE);
        }

        //TODO usar tamaño predefinido con picasso para mejorar rendimiento
        String pictureUrl = streamResultModel.getStreamModel().getPicture();
        imageLoader.loadStreamPicture(pictureUrl, picture);
        separator.setVisibility(showSeparator ? View.VISIBLE : View.GONE);
    }

    private void setClickListener(final StreamResultModel streamResult) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onStreamClickListener.onStreamClick(streamResult);
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View v) {
                return onStreamClickListener.onStreamLongClick(streamResult);
            }
        });
    }

    private String getWatchersText(int watchers) {
        if (showsWatchersText) {
            return itemView.getContext().getResources().getQuantityString(R.plurals.listing_watchers, watchers, watchers);
        } else {
            return String.valueOf(watchers);
        }
    }

    public void renderAuthor(String authorUsername) {
        author.setText(authorUsername);
    }

    public void setShowsWatchersText(Boolean showWatchersText) {
        this.showsWatchersText = showWatchersText;
    }
}
