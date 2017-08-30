package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.shootr.mobile.ui.adapters.listeners.OnFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;

public class WatchableStreamsAdapter extends StreamsListAdapter {

  public WatchableStreamsAdapter(ImageLoader imageLoader, InitialsLoader initialsLoader,
      OnStreamClickListener onStreamClickListener, OnFavoriteClickListener onFavoriteClickListener,
      boolean hasToShowIsFavorite, boolean hasToShowRankNumber) {
    super(imageLoader, initialsLoader, onStreamClickListener, onFavoriteClickListener,
        hasToShowIsFavorite, hasToShowRankNumber);
  }

  @Override
  protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
    return super.onCreateItemViewHolder(parent, viewType);
  }

  @Override protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
    super.onBindItemViewHolder(viewHolder, position);
  }
}
