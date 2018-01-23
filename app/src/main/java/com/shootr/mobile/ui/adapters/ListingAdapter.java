package com.shootr.mobile.ui.adapters;

import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.holders.ListingStreamResultViewHolder;
import com.shootr.mobile.ui.adapters.holders.StreamResultViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.mobile.ui.adapters.sectionedrecyclerview.HeaderViewHolder;
import com.shootr.mobile.ui.adapters.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.NumberFormatUtil;
import java.util.Collections;
import java.util.List;

public class ListingAdapter
    extends SectionedRecyclerViewAdapter<HeaderViewHolder, StreamResultViewHolder> {

  private final ImageLoader imageLoader;
  private final boolean isCurrentUser;
  private final OnStreamClickListener onStreamClickListener;
  private final OnFavoriteClickListener onFavoriteClickListener;
  private final InitialsLoader initialsLoader;
  private final NumberFormatUtil numberFormatUtil;

  private List<StreamResultModel> createdStreams = Collections.emptyList();
  private List<StreamResultModel> followingStreams = Collections.emptyList();

  private boolean showTitles = true;

  public ListingAdapter(ImageLoader imageLoader, boolean isCurrentUser,
      OnStreamClickListener onStreamClickListener, OnFavoriteClickListener onFavoriteClickListener,
      InitialsLoader initialsLoader, NumberFormatUtil numberFormatUtil) {
    this.imageLoader = imageLoader;
    this.isCurrentUser = isCurrentUser;
    this.onStreamClickListener = onStreamClickListener;
    this.onFavoriteClickListener = onFavoriteClickListener;
    this.initialsLoader = initialsLoader;
    this.numberFormatUtil = numberFormatUtil;
  }

  @Override protected int getSectionCount() {
    return 2;
  }

  @Override protected int getItemCountForSection(int section) {
    if (section == 0) {
      return createdStreams.size();
    } else if (section == 1) {
      return followingStreams.size();
    } else {
      throw new IllegalArgumentException(String.format("Section %d not allowed", section));
    }
  }

  @Override
  protected HeaderViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent, int viewType) {
    View text = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_card_title_separator, parent, false);
    return new HeaderViewHolder(text, android.R.id.text1);
  }

  @Override
  protected StreamResultViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_stream, parent, false);

    ListingStreamResultViewHolder listingStreamResultViewHolder =
        new ListingStreamResultViewHolder(view, onStreamClickListener, imageLoader,
            onFavoriteClickListener, initialsLoader, numberFormatUtil);

    if (isCurrentUser) {
      listingStreamResultViewHolder.setShowsFavoritesText(true);
    }
    return listingStreamResultViewHolder;
  }

  @Override protected void onBindSectionHeaderViewHolder(HeaderViewHolder holder, int section) {
    if (showTitles && getItemCountForSection(section) > 0) {
      holder.itemView.findViewById(R.id.separator).setVisibility(View.VISIBLE);
      holder.itemView.findViewById(android.R.id.text1).setVisibility(View.VISIBLE);
      holder.render(getTitleForSection(section));
    } else {
      holder.itemView.findViewById(R.id.separator).setVisibility(View.GONE);
    }
  }

  @StringRes private int getTitleForSection(int section) {
    if (section == 0) {
      return R.string.listing_title_holding;
    } else {
      return R.string.listing_title_favorites;
    }
  }

  @Override
  protected void onBindItemViewHolder(StreamResultViewHolder holder, int section, int position) {
    ((ListingStreamResultViewHolder) holder).setFavorite(isFollowing(section, position));
    StreamResultModel stream = getItem(section, position);
    if (isCurrentUser) {
      holder.render(stream, followingStreams, true, position, false, section == 0);
    } else {
      holder.render(stream, true, position, false);
    }
  }

  private StreamResultModel getItem(int section, int position) {
    if (section == 0) {
      return createdStreams.get(position);
    } else {
      return followingStreams.get(position);
    }
  }

  private boolean isFollowing(int section, int position) {
    return getItem(section, position).getStreamModel().isFollowing();
  }

  public void setCreatedStreams(List<StreamResultModel> streams) {
    this.createdStreams = streams;
    this.notifyDataSetChanged();
  }

  public void setFollowingStreams(List<StreamResultModel> streams) {
    this.followingStreams = streams;
    this.notifyDataSetChanged();
  }

  public void setShowTitles(boolean showTitles) {
    this.showTitles = showTitles;
  }

  public void follow(StreamResultModel streamModel) {
    streamModel.getStreamModel().setFollowing(true);
    if (!followingStreams.contains(streamModel)) {
      followingStreams.add(streamModel);
    }

    notifyDataSetChanged();
  }

  public void unfollow(StreamResultModel streamModel) {
    streamModel.getStreamModel().setFollowing(false);
    if (followingStreams.contains(streamModel)) {
      followingStreams.remove(streamModel);
    }

    notifyDataSetChanged();
  }
}
