package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.DiscoveredType;
import com.shootr.mobile.ui.adapters.holders.DiscoveredStreamViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnDiscoverFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnDiscoveredStreamClickListener;
import com.shootr.mobile.ui.model.DiscoveredModel;
import com.shootr.mobile.util.ImageLoader;
import java.util.List;

public class DiscoverAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private static final int DISCOVERED_STREAM = 0;
  private static final int DISCOVERED_STREAM_SMALL = 1;
  private static final int UNKNOWN = -1;

  private final ImageLoader imageLoader;
  private final OnDiscoveredStreamClickListener onDiscoveredStreamClickListener;
  private final OnDiscoverFavoriteClickListener onFavoriteClickListener;

  private List<DiscoveredModel> items;

  public DiscoverAdapter(ImageLoader imageLoader,
      OnDiscoveredStreamClickListener onDiscoveredStreamClickListener,
      OnDiscoverFavoriteClickListener onFavoriteClickListener) {
    this.imageLoader = imageLoader;
    this.onDiscoveredStreamClickListener = onDiscoveredStreamClickListener;
    this.onFavoriteClickListener = onFavoriteClickListener;
  }

  @Override public int getItemViewType(int position) {
    if (items.get(position).getType().equals(DiscoveredType.STREAM)) {
      return (position % 3 == 0 ? DISCOVERED_STREAM : DISCOVERED_STREAM_SMALL);
    } else {
      return UNKNOWN;
    }
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    if (viewType == DISCOVERED_STREAM) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_discovered_stream, parent, false);
      return new DiscoveredStreamViewHolder(view, imageLoader, onDiscoveredStreamClickListener,
          onFavoriteClickListener);
    } else if (viewType == DISCOVERED_STREAM_SMALL) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_small_discovered_stream, parent, false);
      return new DiscoveredStreamViewHolder(view, imageLoader, onDiscoveredStreamClickListener,
          onFavoriteClickListener);
    } else {
      return null;
    }
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder.getItemViewType() == DISCOVERED_STREAM
        || holder.getItemViewType() == DISCOVERED_STREAM_SMALL) {
      ((DiscoveredStreamViewHolder) holder).render(items.get(position));
    }
  }

  public void setItems(List<DiscoveredModel> discoveredModels) {
    this.items = discoveredModels;
  }

  @Override public int getItemCount() {
    return items != null ? items.size() : 0;
  }
}
