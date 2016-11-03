package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.model.discover.DiscoveredType;
import com.shootr.mobile.ui.adapters.holders.DiscoveredShotViewHolder;
import com.shootr.mobile.ui.adapters.holders.DiscoveredStaticStreamViewHolder;
import com.shootr.mobile.ui.adapters.holders.DiscoveredStreamViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnDiscoveredFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnDiscoveredShotClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnDiscoveredStreamClickListener;
import com.shootr.mobile.ui.model.DiscoveredModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import java.util.List;

public class DiscoverAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private static final int DISCOVERED_STREAM = 0;
  private static final int DISCOVERED_STREAM_SMALL = 1;
  private static final int DISCOVERED_SHOT = 2;
  private static final int DISCOVERED_SHOT_SMALL = 3;
  private static final int DISCOVERED_STATIC_STREAM = 4;

  private static final int UNKNOWN = -1;

  private final ImageLoader imageLoader;
  private final OnDiscoveredStreamClickListener onDiscoveredStreamClickListener;
  private final OnDiscoveredFavoriteClickListener onFavoriteClickListener;
  private final OnDiscoveredShotClickListener onDiscoveredShotClickListener;
  private final OnAvatarClickListener onAvatarClickListener;
  private final AndroidTimeUtils timeUtils;

  private List<DiscoveredModel> items;

  public DiscoverAdapter(ImageLoader imageLoader,
      OnDiscoveredStreamClickListener onDiscoveredStreamClickListener,
      OnDiscoveredFavoriteClickListener onFavoriteClickListener,
      OnDiscoveredShotClickListener onDiscoveredShotClickListener,
      OnAvatarClickListener onAvatarClickListener, AndroidTimeUtils timeUtils) {
    this.imageLoader = imageLoader;
    this.onDiscoveredStreamClickListener = onDiscoveredStreamClickListener;
    this.onFavoriteClickListener = onFavoriteClickListener;
    this.onDiscoveredShotClickListener = onDiscoveredShotClickListener;
    this.onAvatarClickListener = onAvatarClickListener;
    this.timeUtils = timeUtils;
  }

  @Override public int getItemViewType(int position) {
    if (items.get(position).getType().equals(DiscoveredType.STREAM)) {
      return getStreamType(position);
    } else if (items.get(position).getType().equals(DiscoveredType.SHOT)) {
      return (position % 3 == 0 ? DISCOVERED_SHOT : DISCOVERED_SHOT_SMALL);
    } else {
      return UNKNOWN;
    }
  }

  private int getStreamType(int position) {
    if (position % 3 == 0) {
      if (items.get(position).getStreamModel().getLandscapePicture() != null) {
        return DISCOVERED_STATIC_STREAM;
      } else {
        return DISCOVERED_STREAM;
      }
    } else {
      return DISCOVERED_STREAM_SMALL;
    }
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == DISCOVERED_STREAM) {
      return createStreamViewHolder(parent, R.layout.item_discovered_stream);
    } else if (viewType == DISCOVERED_STREAM_SMALL) {
      return createStreamViewHolder(parent, R.layout.item_small_discovered_stream);
    } else if (viewType == DISCOVERED_SHOT) {
      return createShotViewHolder(parent, R.layout.item_discovered_shot);
    } else if (viewType == DISCOVERED_SHOT_SMALL) {
      return createShotViewHolder(parent, R.layout.item_small_discovered_shot);
    } else if (viewType == DISCOVERED_STATIC_STREAM) {
      return createStaticStreamViewHolder(parent, R.layout.item_discovered_static_stream);
    } else {
      return null;
    }
  }

  private RecyclerView.ViewHolder createShotViewHolder(ViewGroup parent, int itemDiscoveredStream) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(itemDiscoveredStream, parent, false);
    return new DiscoveredShotViewHolder(view, imageLoader,
        onDiscoveredShotClickListener, onAvatarClickListener,
        onFavoriteClickListener, onDiscoveredStreamClickListener, timeUtils);
  }

  private RecyclerView.ViewHolder createStreamViewHolder(ViewGroup parent,
      int itemDiscoveredStream) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(itemDiscoveredStream, parent, false);
    return new DiscoveredStreamViewHolder(view, imageLoader, onDiscoveredStreamClickListener,
        onFavoriteClickListener);
  }

  private RecyclerView.ViewHolder createStaticStreamViewHolder(ViewGroup parent,
      int itemDiscoveredStream) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(itemDiscoveredStream, parent, false);
    return new DiscoveredStaticStreamViewHolder(view, imageLoader, onDiscoveredStreamClickListener,
        onFavoriteClickListener);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (isStreamTypes(holder)) {
      if (holder.getItemViewType() == DISCOVERED_STATIC_STREAM) {
        ((DiscoveredStaticStreamViewHolder) holder).render(items.get(position));
      } else {
        ((DiscoveredStreamViewHolder) holder).render(items.get(position),
            holder.getItemViewType() == DISCOVERED_STREAM_SMALL);
      }
    } else if (isShotTypes(holder)) {
      ((DiscoveredShotViewHolder) holder).render(items.get(position));
    }
  }

  private boolean isShotTypes(RecyclerView.ViewHolder holder) {
    return holder.getItemViewType() == DISCOVERED_SHOT
        || holder.getItemViewType() == DISCOVERED_SHOT_SMALL;
  }

  private boolean isStreamTypes(RecyclerView.ViewHolder holder) {
    return holder.getItemViewType() == DISCOVERED_STREAM
        || holder.getItemViewType() == DISCOVERED_STREAM_SMALL
        || holder.getItemViewType() == DISCOVERED_STATIC_STREAM;
  }

  public void setItems(List<DiscoveredModel> discoveredModels) {
    this.items = discoveredModels;
  }

  @Override public int getItemCount() {
    return items != null ? items.size() : 0;
  }
}
