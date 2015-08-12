package com.shootr.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.adapters.listeners.OnUnwatchClickListener;
import com.shootr.android.ui.adapters.recyclerview.SubheaderRecyclerViewAdapter;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.util.PicassoWrapper;
import java.util.List;

public class StreamsListAdapter extends SubheaderRecyclerViewAdapter<RecyclerView.ViewHolder, StreamResultModel, StreamResultModel> {

    private final PicassoWrapper picasso;

    private OnStreamClickListener onStreamClickListener;
    private OnUnwatchClickListener onUnwatchClickListener;

    public StreamsListAdapter(PicassoWrapper picasso, OnStreamClickListener onStreamClickListener) {
        this.picasso = picasso;
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

    @Override
    protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_stream, parent, false);
        return new WatchingStreamResultViewHolder(view, onStreamClickListener, picasso, onUnwatchClickListener);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateSubheaderViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_card_separator,
          parent,
          false);
        return new SubheaderViewHolder(view);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_stream, parent, false);
        return new StreamResultViewHolder(view, onStreamClickListener, picasso);
    }

    @Override
    protected void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        StreamResultModel stream = getHeader();
        ((StreamResultViewHolder) viewHolder).render(stream);
    }

    @Override
    protected void onBindSubheaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        /* no-op */
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        StreamResultModel stream = getItem(position);
        ((StreamResultViewHolder) viewHolder).render(stream);
    }

    public void setCurrentWatchingStream(StreamResultModel streamResultModel) {
        this.setHeader(streamResultModel);
    }

    public void setOnUnwatchClickListener(OnUnwatchClickListener onUnwatchClickListener) {
        this.onUnwatchClickListener = onUnwatchClickListener;
    }
}
