package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.ui.adapters.listeners.OnBoardingFavoriteClickListener;
import com.shootr.mobile.ui.model.OnBoardingModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.widgets.AvatarView;
import com.shootr.mobile.ui.widgets.FollowButton;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;

public class OnBoardingUserViewHolder extends RecyclerView.ViewHolder {

  private final OnBoardingFavoriteClickListener onFavoriteClickListener;
  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;

  @BindView(R.id.user_avatar) AvatarView avatar;
  @BindView(R.id.user_name) TextView title;
  @BindView(R.id.user_username) TextView subtitle;
  @BindView(R.id.user_follow_button) FollowButton followButton;


  public OnBoardingUserViewHolder(View itemView,
      OnBoardingFavoriteClickListener onFavoriteClickListener, ImageLoader imageLoader,
      InitialsLoader initialsLoader) {
    super(itemView);
    this.onFavoriteClickListener = onFavoriteClickListener;
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    ButterKnife.bind(this, itemView);
  }

  public void render(OnBoardingModel onBoardingModel) {
    this.setupFavoriteClickListener(onBoardingModel);
    title.setText(onBoardingModel.getStreamModel().getTitle());
    handleFavorite(onBoardingModel);
    setTitle(onBoardingModel.getUserModel());
    setupUsername(getUsernameForSubtitle(onBoardingModel.getUserModel()));
    setupPhoto(onBoardingModel.getUserModel());
  }

  private void handleFavorite(OnBoardingModel onBoardingStreamModel) {
    if (onBoardingStreamModel.isFavorite()) {
      showIsFollowing();
    } else {
      showIsNotFollowing();
    }
  }

  private void showIsFollowing() {
    followButton.setVisibility(View.VISIBLE);
    followButton.setFollowing(true);
  }

  private void showIsNotFollowing() {
    followButton.setVisibility(View.VISIBLE);
    followButton.setFollowing(false);
  }

  private void setupPhoto(UserModel userModel) {
    String photo = userModel.getPhoto();
    imageLoader.loadProfilePhoto(photo, avatar, userModel.getUsername());
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

  private void setupFavoriteClickListener(final OnBoardingModel onBoardingUserModel) {
    if (onFavoriteClickListener != null) {
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          handleFavoriteStatus(onBoardingUserModel);
        }
      });
      followButton.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          handleFavoriteStatus(onBoardingUserModel);
        }
      });
    }
  }

  private void handleFavoriteStatus(OnBoardingModel onBoardingStreamModel) {
    if (onBoardingStreamModel.isFavorite()) {
      onFavoriteClickListener.onRemoveFavoriteClick(onBoardingStreamModel);
      followButton.setFollowing(false);
    } else {
      onFavoriteClickListener.onFavoriteClick(onBoardingStreamModel);
      followButton.setFollowing(true);
    }
  }
}
