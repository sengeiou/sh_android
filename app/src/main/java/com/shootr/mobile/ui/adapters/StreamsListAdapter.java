package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUnwatchClickListener;
import com.shootr.mobile.ui.adapters.recyclerview.SubheaderRecyclerViewAdapter;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.util.ImageLoader;
import java.util.List;

public class StreamsListAdapter
  extends SubheaderRecyclerViewAdapter<RecyclerView.ViewHolder, StreamResultModel, StreamResultModel> {

    private final ImageLoader imageLoader;

    private OnStreamClickListener onStreamClickListener;
    private OnUnwatchClickListener onUnwatchClickListener;
    private List<String> mutedStreamsIds;

    public StreamsListAdapter(ImageLoader imageLoader, OnStreamClickListener onStreamClickListener) {
        this.imageLoader = imageLoader;
        this.onStreamClickListener = onStreamClickListener;
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

    @Override protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_stream, parent, false);
        StreamResultViewHolder watchingViewHolder =
          new StreamResultViewHolder(view, onStreamClickListener, imageLoader, mutedStreamsIds);
        watchingViewHolder.enableWatchingState(onUnwatchClickListener);
        return watchingViewHolder;
    }

    @Override protected RecyclerView.ViewHolder onCreateSubheaderViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_card_separator, parent, false);
        return new SubheaderViewHolder(view);
    }

    @Override protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_stream, parent, false);
        return new StreamResultViewHolder(view, onStreamClickListener, imageLoader, mutedStreamsIds);
    }

    @Override protected void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        StreamResultModel stream = getHeader();
        ((StreamResultViewHolder) viewHolder).setMutedStreamIds(mutedStreamsIds);
        ((StreamResultViewHolder) viewHolder).render(stream, false);
    }

    @Override protected void onBindSubheaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        /* no-op */
    }

    @Override protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        StreamResultModel stream = getItem(position);
        boolean showSeparator = position != getFirstItemPosition();
        ((StreamResultViewHolder) viewHolder).setMutedStreamIds(mutedStreamsIds);
        ((StreamResultViewHolder) viewHolder).render(stream, showSeparator);
    }

    public void setCurrentWatchingStream(StreamResultModel streamResultModel) {
        this.setHeader(streamResultModel);
    }

    public void setOnUnwatchClickListener(OnUnwatchClickListener onUnwatchClickListener) {
        this.onUnwatchClickListener = onUnwatchClickListener;
    }

    public OnUnwatchClickListener getOnUnwatchClickListener() {
        return onUnwatchClickListener;
    }

    public void setMutedStreamIds(List<String> mutedStreamIds) {
        this.mutedStreamsIds = mutedStreamIds;
    }

    public List<String> getMutedStreamIds() {
        return mutedStreamsIds;
    }
}
