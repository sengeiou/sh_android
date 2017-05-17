package com.shootr.mobile.ui.adapters.holders;

import android.view.View;
import com.shootr.mobile.ui.adapters.listeners.OnFollowUnfollowListener;
import com.shootr.mobile.ui.adapters.listeners.OnUserClickListener;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.util.ImageLoader;

public class ParticipantViewHolder extends UserSearchViewHolder {
  public ParticipantViewHolder(View itemView, ImageLoader imageLoader,
      OnFollowUnfollowListener onFollowUnfollowListener, OnUserClickListener onUserClickListener) {
    super(itemView, imageLoader, onFollowUnfollowListener, onUserClickListener);
  }

  @Override protected String getSubtitle(UserModel item) {
    return item.getJoinStreamDate();
  }
}
