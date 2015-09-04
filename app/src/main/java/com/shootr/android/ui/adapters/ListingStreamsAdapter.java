package com.shootr.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnFavoriteClickListener;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.util.ImageLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListingStreamsAdapter extends StreamsListAdapter {

    private final ImageLoader imageLoader;

    private boolean isCurrentUser;
    private OnStreamClickListener onStreamClickListener;
    private OnFavoriteClickListener onFavoriteClickListener;
    private Set<String> favoriteStreamsIds;

    public ListingStreamsAdapter(ImageLoader imageLoader, boolean isCurrentUser, OnStreamClickListener onStreamClickListener,
      OnFavoriteClickListener onFavoriteClickListener) {
        super(imageLoader, onStreamClickListener);
        this.imageLoader = imageLoader;
        this.isCurrentUser = isCurrentUser;
        this.onStreamClickListener = onStreamClickListener;
        this.onFavoriteClickListener = onFavoriteClickListener;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        int layoutRes = isCurrentUser ? R.layout.item_list_listing_stream : R.layout.item_list_stream;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);

        ListingStreamResultViewHolder listingStreamResultViewHolder =
          new ListingStreamResultViewHolder(view, onStreamClickListener, imageLoader, onFavoriteClickListener);

        if (isCurrentUser) {
            listingStreamResultViewHolder.setShowsWatchersText(isCurrentUser);
        }
        return listingStreamResultViewHolder;
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((ListingStreamResultViewHolder) viewHolder).setFavorite(isFavorite(position));
        StreamResultModel stream = getItem(position);
        boolean showSeparator = position != getFirstItemPosition();
        if (isCurrentUser) {
            ((StreamResultViewHolder) viewHolder).render(stream, showSeparator);
        } else {
            ((StreamResultViewHolder) viewHolder).render(stream, showSeparator);
        }
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
