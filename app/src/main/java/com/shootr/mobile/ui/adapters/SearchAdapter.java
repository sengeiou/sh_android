package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.ui.adapters.holders.StreamSearchViewHolder;
import com.shootr.mobile.ui.adapters.holders.UserSearchViewHolder;
import com.shootr.mobile.ui.adapters.listeners.FavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnFollowUnfollowListener;
import com.shootr.mobile.ui.adapters.listeners.OnSearchStreamClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUserClickListener;
import com.shootr.mobile.ui.model.SearchableModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private static final int TYPE_STREAM = 0;
  private static final int TYPE_USER = 1;
  private static final int UNKNOWN = -1;

  private final ImageLoader imageLoader;
  private final OnFollowUnfollowListener onFollowUnfollowListener;
  private final OnUserClickListener onUserClickListener;
  private final OnSearchStreamClickListener onStreamClickListener;
  private final FavoriteClickListener onFavoriteClickListener;
  private final InitialsLoader initialsLoader;

  private List<SearchableModel> items;

  public SearchAdapter(ImageLoader imageLoader, InitialsLoader initialsLoader,
      OnFollowUnfollowListener onFollowUnfollowListener, OnUserClickListener onUserClickListener,
      OnSearchStreamClickListener onStreamClickListener,
      FavoriteClickListener onFavoriteClickListener) {
    this.imageLoader = imageLoader;
    this.onFollowUnfollowListener = onFollowUnfollowListener;
    this.onUserClickListener = onUserClickListener;
    this.onStreamClickListener = onStreamClickListener;
    this.onFavoriteClickListener = onFavoriteClickListener;
    this.initialsLoader = initialsLoader;
  }

  @Override public int getItemViewType(int position) {
    if (items.get(position) instanceof StreamModel) {
      return TYPE_STREAM;
    } else if (items.get(position) instanceof UserModel) {
      return TYPE_USER;
    } else {
      return UNKNOWN;
    }
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    RecyclerView.ViewHolder viewHolder = null;
    View view;

    if (viewType == TYPE_STREAM) {
      view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_list_stream, parent, false);
      viewHolder = new StreamSearchViewHolder(view, onStreamClickListener, onFavoriteClickListener,
          imageLoader, initialsLoader);
    } else if (viewType == TYPE_USER) {
      view =
          LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_user, parent, false);
      viewHolder = new UserSearchViewHolder(view, imageLoader, onFollowUnfollowListener,
          onUserClickListener);
    }

    return viewHolder;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder.getItemViewType() == TYPE_STREAM) {
      ((StreamSearchViewHolder) holder).render((StreamModel) items.get(position));
    } else if (holder.getItemViewType() == TYPE_USER) {
      ((UserSearchViewHolder) holder).render((UserModel) items.get(position));
    }
  }

  @Override public int getItemCount() {
    return items != null ? items.size() : 0;
  }

  public void setItems(List<SearchableModel> searchableModels) {
    this.items = searchableModels;
  }

  public void followUser(UserModel user) {
    int index = items.indexOf(user);
    ((UserModel) items.get(index)).setRelationship(FollowEntity.RELATIONSHIP_FOLLOWING);
    notifyDataSetChanged();
  }

  public void unfollowUser(UserModel user) {
    int index = items.indexOf(user);
    ((UserModel) items.get(index)).setRelationship(FollowEntity.RELATIONSHIP_NONE);
    notifyDataSetChanged();
  }

  public void markFavorite(StreamModel stream) {
    int index = items.indexOf(stream);
    ((StreamModel) items.get(index)).setFavorite(true);
  }

  public void unmarkFavorite(StreamModel stream) {
    int index = items.indexOf(stream);
    ((StreamModel) items.get(index)).setFavorite(false);
    notifyDataSetChanged();
  }

  public void addItems(List<SearchableModel> items) {
    this.items.addAll(items);
    notifyDataSetChanged();
  }
}
