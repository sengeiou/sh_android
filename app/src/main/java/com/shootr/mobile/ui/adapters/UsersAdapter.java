package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.ui.adapters.holders.UserSearchViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnFollowUnfollowListener;
import com.shootr.mobile.ui.adapters.listeners.OnUserClickListener;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  final ImageLoader imageLoader;
  final OnFollowUnfollowListener onFollowUnfollowListener;
  final OnUserClickListener onUserClickListener;
  final InitialsLoader initialsLoader;

  List<UserModel> users;

  public UsersAdapter(ImageLoader imageLoader, InitialsLoader initialsLoader,
      OnFollowUnfollowListener onFollowUnfollowListener, OnUserClickListener onUserClickListener) {
    this.imageLoader = imageLoader;
    this.onFollowUnfollowListener = onFollowUnfollowListener;
    this.onUserClickListener = onUserClickListener;
    this.initialsLoader = initialsLoader;
    users = new ArrayList<>();
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    RecyclerView.ViewHolder viewHolder;
    View view;

    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_user, parent, false);
    viewHolder =
        new UserSearchViewHolder(view, imageLoader, onFollowUnfollowListener, onUserClickListener);

    return viewHolder;
  }

  public void addItems(List<UserModel> users) {
    this.users.addAll(users);
  }

  public List<UserModel> getItems() {
    return users;
  }

  public UserModel getItem(int position) {
    return users.get(position);
  }

  public void removeItems() {
    this.users = Collections.emptyList();
  }


  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    ((UserSearchViewHolder) holder).render(users.get(position));
  }

  @Override public int getItemCount() {
    return users != null ? users.size() : 0;
  }

  public void setUsers(List<UserModel> searchableModels) {
    this.users = searchableModels;
  }

  public void followUser(UserModel user) {
    int index = users.indexOf(user);
    (users.get(index)).setRelationship(FollowEntity.RELATIONSHIP_FOLLOWING);
    notifyDataSetChanged();
  }

  public void unfollowUser(UserModel user) {
    int index = users.indexOf(user);
    (users.get(index)).setRelationship(FollowEntity.RELATIONSHIP_NONE);
    notifyDataSetChanged();
  }
}
