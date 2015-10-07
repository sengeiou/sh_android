package com.shootr.android.ui.adapters;

import android.support.annotation.IntegerRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnFavoriteClickListener;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.adapters.sectionedrecyclerview.HeaderViewHolder;
import com.shootr.android.ui.adapters.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.util.ImageLoader;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListingAdapter extends SectionedRecyclerViewAdapter<HeaderViewHolder, StreamResultViewHolder> {

    private final ImageLoader imageLoader;
    private final boolean isCurrentUser;
    private final OnStreamClickListener onStreamClickListener;
    private final OnFavoriteClickListener onFavoriteClickListener;

    private List<StreamResultModel> createdStreams = Collections.emptyList();
    private List<StreamResultModel> favoritedStreams = Collections.emptyList();

    private Set<String> favoriteStreamsIds = Collections.emptySet();
    private boolean showTitles = true;

    public ListingAdapter(ImageLoader imageLoader,
      boolean isCurrentUser,
      OnStreamClickListener onStreamClickListener,
      OnFavoriteClickListener onFavoriteClickListener) {
        this.imageLoader = imageLoader;
        this.isCurrentUser = isCurrentUser;
        this.onStreamClickListener = onStreamClickListener;
        this.onFavoriteClickListener = onFavoriteClickListener;
    }

    @Override
    protected int getSectionCount() {
        return 2;
    }

    @Override
    protected int getItemCountForSection(int section) {
        if (section == 0) {
            return createdStreams.size();
        } else if (section == 1) {
            return favoritedStreams.size();
        } else {
            throw new IllegalArgumentException(String.format("Section %d not allowed", section));
        }
    }

    @Override
    protected HeaderViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent, int viewType) {
        View text = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_title_separator, parent, false);
        return new HeaderViewHolder(text, android.R.id.text1);
    }

    @Override
    protected StreamResultViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        int layoutRes = isCurrentUser ? R.layout.item_list_listing_stream : R.layout.item_list_stream;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);

        ListingStreamResultViewHolder listingStreamResultViewHolder =
          new ListingStreamResultViewHolder(view, onStreamClickListener, imageLoader, onFavoriteClickListener);

        if (isCurrentUser) {
            listingStreamResultViewHolder.setShowsWatchersText(true);
        }
        return listingStreamResultViewHolder;
    }

    @Override
    protected void onBindSectionHeaderViewHolder(HeaderViewHolder holder, int section) {
        if (showTitles && getItemCountForSection(section) > 0) {
            holder.itemView.findViewById(R.id.separator).setVisibility(View.VISIBLE);
            holder.itemView.findViewById(android.R.id.text1).setVisibility(View.VISIBLE);
            holder.render(getTitleForSection(section));
        } else {
            holder.itemView.findViewById(R.id.separator).setVisibility(View.GONE);
        }
    }

    @IntegerRes
    private int getTitleForSection(int section) {
        if (section == 0) {
            return R.string.listing_title_holding;
        } else {
            return R.string.listing_title_favorites;
        }
    }

    @Override
    protected void onBindItemViewHolder(StreamResultViewHolder holder, int section, int position) {
        ((ListingStreamResultViewHolder) holder).setFavorite(isFavorite(section, position));
        StreamResultModel stream = getItem(section, position);
        boolean showSeparator = position != 0;
        if (isCurrentUser) {
            holder.render(stream, showSeparator);
        } else {
            holder.render(stream, showSeparator);
        }
    }

    private StreamResultModel getItem(int section, int position) {
        if (section == 0) {
            return createdStreams.get(position);
        } else {
            return favoritedStreams.get(position);
        }
    }

    private boolean isFavorite(int section, int position) {
        String idStream = getItem(section, position).getStreamModel().getIdStream();
        return favoriteStreamsIds != null && favoriteStreamsIds.contains(idStream);
    }

    public void setFavoriteStreams(List<StreamResultModel> favoriteStreams) {
        favoriteStreamsIds = new HashSet<>(favoriteStreams.size());
        for (StreamResultModel stream : favoriteStreams) {
            favoriteStreamsIds.add(stream.getStreamModel().getIdStream());
        }
    }

    public void setCreatedStreams(List<StreamResultModel> streams) {
        this.createdStreams = streams;
        this.notifyDataSetChanged();
    }

    public void setFavoritedStreams(List<StreamResultModel> streams) {
        this.favoritedStreams = streams;
        this.notifyDataSetChanged();
    }

    public void setShowTitles(boolean showTitles) {
        this.showTitles = showTitles;
    }
}
