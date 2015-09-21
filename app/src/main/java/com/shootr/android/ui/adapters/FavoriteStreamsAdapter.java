package com.shootr.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.util.ImageLoader;

public class FavoriteStreamsAdapter extends StreamsListAdapter {

    public FavoriteStreamsAdapter(ImageLoader imageLoader, OnStreamClickListener onStreamClickListener) {
        super(imageLoader, onStreamClickListener);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return super.onCreateItemViewHolder(parent, viewType);
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        StreamResultViewHolder streamViewHolder = (StreamResultViewHolder) viewHolder;
        if (isWatchingStream(position)) {
            streamViewHolder.enableWatchingState(getOnUnwatchClickListener());
        } else {
            streamViewHolder.disableWatchingState();
        }
        super.onBindItemViewHolder(viewHolder, position);
    }

    private boolean isWatchingStream(int position) {
        return getItem(position).isWatching();
    }
}
