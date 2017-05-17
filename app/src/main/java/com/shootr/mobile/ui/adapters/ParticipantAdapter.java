package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.holders.ParticipantViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnFollowUnfollowListener;
import com.shootr.mobile.ui.adapters.listeners.OnUserClickListener;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;

public class ParticipantAdapter extends UsersAdapter {
  public ParticipantAdapter(ImageLoader imageLoader, InitialsLoader initialsLoader,
      OnFollowUnfollowListener onFollowUnfollowListener, OnUserClickListener onUserClickListener) {
    super(imageLoader, initialsLoader, onFollowUnfollowListener, onUserClickListener);
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    RecyclerView.ViewHolder viewHolder;
    View view;

    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_user, parent, false);
    viewHolder =
        new ParticipantViewHolder(view, imageLoader, onFollowUnfollowListener, onUserClickListener);

    return viewHolder;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    ((ParticipantViewHolder) holder).render((UserModel) users.get(position));
  }
}
