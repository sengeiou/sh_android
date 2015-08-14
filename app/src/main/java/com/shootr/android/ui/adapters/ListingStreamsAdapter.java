package com.shootr.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnFavoriteClickListener;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.util.PicassoWrapper;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListingStreamsAdapter extends StreamsListAdapter {

    private final PicassoWrapper picasso;

    private OnStreamClickListener onStreamClickListener;
    private OnFavoriteClickListener onFavoriteClickListener;
    private Set<String> favoriteStreamsIds;

    public ListingStreamsAdapter(PicassoWrapper picasso,
      OnStreamClickListener onStreamClickListener,
      OnFavoriteClickListener onFavoriteClickListener) {
        super(picasso, onStreamClickListener);
        this.picasso = picasso;
        this.onStreamClickListener = onStreamClickListener;
        this.onFavoriteClickListener = onFavoriteClickListener;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_stream, parent, false);
        return new ListingStreamResultViewHolder(view, onStreamClickListener, picasso, onFavoriteClickListener);
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((ListingStreamResultViewHolder) viewHolder).setFavorite(isFavorite(position));
        super.onBindItemViewHolder(viewHolder, position);
    }

    private boolean isFavorite(int position) {
        String streamId = getItem(position).getStreamModel().getIdStream();
        return favoriteStreamsIds != null && favoriteStreamsIds.contains(streamId);
    }

    public void setFavoriteStreams(List<StreamResultModel> favoriteStreams) {
        favoriteStreamsIds = new HashSet<>(favoriteStreams.size());
        for (StreamResultModel stream : favoriteStreams) {
            favoriteStreamsIds.add(stream.getStreamModel().getIdStream());
        }
    }
}
