package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.ui.adapters.listeners.OnFollowUnfollowListener;
import com.shootr.mobile.ui.adapters.listeners.OnUserClickListener;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.widgets.FollowButton;
import com.shootr.mobile.util.ImageLoader;

public class UserSearchViewHolder extends RecyclerView.ViewHolder {

  private final ImageLoader imageLoader;
  private final OnFollowUnfollowListener onFollowUnfollowListener;
  private final OnUserClickListener onUserClickListener;

  @BindView(R.id.user_avatar) ImageView avatar;
  @BindView(R.id.user_name) TextView title;
  @BindView(R.id.user_username) TextView subtitle;
  @BindView(R.id.user_follow_button) FollowButton followButton;

  private View view;

  public UserSearchViewHolder(View itemView, ImageLoader imageLoader,
      OnFollowUnfollowListener onFollowUnfollowListener, final OnUserClickListener onUserClickListener) {
    super(itemView);
    view = itemView;
    this.imageLoader = imageLoader;
    this.onFollowUnfollowListener = onFollowUnfollowListener;
    this.onUserClickListener = onUserClickListener;
    ButterKnife.bind(this, itemView);
  }

  public void render(final UserModel userModel) {
    setTitle(userModel);
    setupUsername(getSubtitle(userModel));
    setupFollowUnfollow(userModel);
    setupPhoto(userModel);
    view.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onUserClickListener.onUserClick(userModel.getIdUser());
      }
    });
  }

  private void setupPhoto(UserModel userModel) {
    String photo = userModel.getPhoto();
    imageLoader.loadProfilePhoto(photo, avatar);
  }

  private void setupFollowUnfollow(final UserModel userModel) {
    if (userModel.getRelationship() == FollowEntity.RELATIONSHIP_FOLLOWING) {
      followButton.setVisibility(View.VISIBLE);
      followButton.setFollowing(true);
    } else if (userModel.getRelationship() == FollowEntity.RELATIONSHIP_OWN) {
      followButton.setVisibility(View.GONE);
      followButton.setEditProfile();
    } else {
      followButton.setVisibility(View.VISIBLE);
      followButton.setFollowing(false);
    }
    followButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (followButton.isFollowing()) {
          if (onFollowUnfollowListener != null) {
            onFollowUnfollowListener.onUnfollow(userModel);
          }
        } else {
          if (onFollowUnfollowListener != null) {
            onFollowUnfollowListener.onFollow(userModel);
          }
        }
      }
    });
  }

  private String getSubtitle(UserModel item) {
    return getUsernameForSubtitle(item);
  }

  private String getUsernameForSubtitle(UserModel item) {
    return String.format("@%s", item.getUsername());
  }

  private void setupUsername(String username) {
    subtitle.setText(username);
    subtitle.setVisibility(View.VISIBLE);
  }

  private void setTitle(UserModel userModel) {
    title.setText(userModel.getName());
    if (userModel.isVerifiedUser()) {
      title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_verified_user_list, 0);
      title.setCompoundDrawablePadding(6);
    } else {
      title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }
  }
}
