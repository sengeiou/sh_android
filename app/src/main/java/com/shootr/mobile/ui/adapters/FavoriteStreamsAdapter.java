package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.shootr.mobile.ui.adapters.holders.StreamResultViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;

public class FavoriteStreamsAdapter extends StreamsListAdapter {

  public FavoriteStreamsAdapter(ImageLoader imageLoader, InitialsLoader initialsLoader,
      OnStreamClickListener onStreamClickListener) {
    super(imageLoader, initialsLoader, onStreamClickListener);
  }

  @Override
  protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
    return super.onCreateItemViewHolder(parent, viewType);
  }

  @Override protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
    StreamResultViewHolder streamViewHolder = (StreamResultViewHolder) viewHolder;
    streamViewHolder.setMutedStreamIds(getMutedStreamIds());
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
