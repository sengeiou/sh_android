package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.shootr.mobile.ui.adapters.holders.StreamResultViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;

public class WatchableStreamsAdapter extends StreamsListAdapter {

  public WatchableStreamsAdapter(ImageLoader imageLoader, InitialsLoader initialsLoader,
      OnStreamClickListener onStreamClickListener, OnFavoriteClickListener onFavoriteClickListener,
      boolean hasToShowIsFavorite) {
    super(imageLoader, initialsLoader, onStreamClickListener, onFavoriteClickListener, hasToShowIsFavorite);
  }

  @Override
  protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
    return super.onCreateItemViewHolder(parent, viewType);
  }

  @Override protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
    StreamResultViewHolder streamViewHolder = (StreamResultViewHolder) viewHolder;
    streamViewHolder.setMutedStreamIds(getMutedStreamIds());
    super.onBindItemViewHolder(viewHolder, position);
  }
}
