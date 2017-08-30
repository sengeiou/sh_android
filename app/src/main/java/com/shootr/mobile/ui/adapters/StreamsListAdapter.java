package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.holders.StreamResultViewHolder;
import com.shootr.mobile.ui.adapters.holders.SubheaderViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUnwatchClickListener;
import com.shootr.mobile.ui.adapters.recyclerview.SubheaderRecyclerViewAdapter;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import java.util.List;

public class StreamsListAdapter extends
    SubheaderRecyclerViewAdapter<RecyclerView.ViewHolder, StreamResultModel, StreamResultModel> {

  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;
  private boolean hasToShowIsFavorite = true;
  private boolean hasToShowRankNumber = true;

  private OnStreamClickListener onStreamClickListener;
  private OnUnwatchClickListener onUnwatchClickListener;
  private OnFavoriteClickListener onFavoriteClickListener;

  public StreamsListAdapter(ImageLoader imageLoader, InitialsLoader initialsLoader,
      OnStreamClickListener onStreamClickListener, OnFavoriteClickListener onFavoriteClickListener,
      boolean hasToShowIsFavorite, boolean hasToShowRankNumber) {
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    this.onStreamClickListener = onStreamClickListener;
    this.onFavoriteClickListener = onFavoriteClickListener;
    this.hasToShowIsFavorite = hasToShowIsFavorite;
    this.hasToShowRankNumber = hasToShowRankNumber;
  }

  public void setStreams(List<StreamResultModel> streams) {
    boolean wasEmpty = getItems().isEmpty();
    setItems(streams);
    if (wasEmpty) {
      notifyItemRangeInserted(0, streams.size());
    } else {
      notifyDataSetChanged();
    }
  }

  @Override
  protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stream_list, parent, false);
    StreamResultViewHolder watchingViewHolder =
        new StreamResultViewHolder(view, onStreamClickListener, onFavoriteClickListener,
            imageLoader, initialsLoader);
    watchingViewHolder.enableWatchingState(onUnwatchClickListener);
    return watchingViewHolder;
  }

  @Override
  protected RecyclerView.ViewHolder onCreateSubheaderViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_list_card_separator, parent, false);
    return new SubheaderViewHolder(view);
  }

  @Override
  protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stream_list, parent, false);
    return new StreamResultViewHolder(view, onStreamClickListener, onFavoriteClickListener,
        imageLoader, initialsLoader);
  }

  @Override
  protected void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
    StreamResultModel stream = getHeader();
    ((StreamResultViewHolder) viewHolder).render(stream, hasToShowIsFavorite, position, hasToShowRankNumber);
  }

  @Override protected void onBindSubheaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        /* no-op */
  }

  @Override protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
    StreamResultModel stream = getItem(position);
    ((StreamResultViewHolder) viewHolder).render(stream, hasToShowIsFavorite,
        position, hasToShowRankNumber);
  }

  public void setOnUnwatchClickListener(OnUnwatchClickListener onUnwatchClickListener) {
    this.onUnwatchClickListener = onUnwatchClickListener;
  }
}
