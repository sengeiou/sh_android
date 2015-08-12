package com.shootr.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnFavoriteClickListener;
import com.shootr.android.ui.adapters.listeners.OnRemoveFavoriteClickListener;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.adapters.recyclerview.SubheaderRecyclerViewAdapter;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.util.PicassoWrapper;
import java.util.ArrayList;
import java.util.List;

public class ListingStreamsAdapter extends StreamsListAdapter {

    private final PicassoWrapper picasso;

    private OnStreamClickListener onStreamClickListener;
    private OnFavoriteClickListener onFavoriteClickListener;
    private OnRemoveFavoriteClickListener onRemoveFavoriteClickListener;
    private List<StreamResultModel> favoriteStreams;

    public ListingStreamsAdapter(PicassoWrapper picasso, OnStreamClickListener onStreamClickListener,
      OnFavoriteClickListener onFavoriteClickListener, OnRemoveFavoriteClickListener onRemoveFavoriteClickListener) {
        super(picasso, onStreamClickListener);
        this.picasso = picasso;
        this.onStreamClickListener = onStreamClickListener;
        this.onFavoriteClickListener = onFavoriteClickListener;
        this.onRemoveFavoriteClickListener = onRemoveFavoriteClickListener;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_listing_stream, parent, false);
        return new ListingStreamResultViewHolder(view, onStreamClickListener, picasso, onFavoriteClickListener,
          onRemoveFavoriteClickListener);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateSubheaderViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_card_separator, parent, false);
        return new SubheaderViewHolder(view);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_listing_stream, parent, false);
        return new ListingStreamResultViewHolder(view, onStreamClickListener, picasso, onFavoriteClickListener,
          onRemoveFavoriteClickListener);
    }

    @Override
    protected void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        StreamResultModel stream = getHeader();

        ListingStreamResultViewHolder listingStreamResultViewHolder =
          new ListingStreamResultViewHolder(viewHolder.itemView, onStreamClickListener, picasso,
            onFavoriteClickListener, onRemoveFavoriteClickListener);

        listingStreamResultViewHolder.render(stream);
    }

    @Override
    protected void onBindSubheaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        /* no-op */
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        StreamResultModel stream = getItems().get(position);

        List<String> favoriteIds = new ArrayList<>();

        for (StreamResultModel favoriteStream : favoriteStreams) {
            favoriteIds.add(favoriteStream.getStreamModel().getIdStream());
        }

        if(favoriteIds.contains(stream.getStreamModel().getIdStream())) {
            ((ListingStreamResultViewHolder) viewHolder).render(stream, position, true);
        } else {
            ((ListingStreamResultViewHolder) viewHolder).render(stream, position, false);
        }

        ((ListingStreamResultViewHolder) viewHolder).render(stream);
    }

    public void setFavoriteStreams(List<StreamResultModel> favoriteStreams) {
        this.favoriteStreams = favoriteStreams;
    }
}
