package com.shootr.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnFavoriteClickListener;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.adapters.recyclerview.SubheaderRecyclerViewAdapter;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.util.PicassoWrapper;
import java.util.List;

public class ListingStreamsAdapter extends SubheaderRecyclerViewAdapter<RecyclerView.ViewHolder, StreamResultModel, StreamResultModel> {

    private final PicassoWrapper picasso;

    private OnStreamClickListener onStreamClickListener;
    private OnFavoriteClickListener onFavoriteClickListener;

    public ListingStreamsAdapter(PicassoWrapper picasso, OnStreamClickListener onStreamClickListener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_listing_stream, parent, false);
        return new StreamResultViewHolder(view, onStreamClickListener, picasso);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_listing_stream, parent, false);
        return new StreamResultViewHolder(view, onStreamClickListener, picasso);
    }

    @Override
    protected void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        StreamResultModel stream = getHeader();

        ListingStreamResultViewHolder listingStreamResultViewHolder =
          new ListingStreamResultViewHolder(viewHolder.itemView, onStreamClickListener, picasso,
            onFavoriteClickListener);

        listingStreamResultViewHolder.render(stream, position);
    }

    @Override
    protected void onBindSubheaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        /* no-op */
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        StreamResultModel stream = getItem(position);
        ((StreamResultViewHolder) viewHolder).render(stream, position);
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener onFavoriteClickListener) {
        this.onFavoriteClickListener = onFavoriteClickListener;
    }

}
