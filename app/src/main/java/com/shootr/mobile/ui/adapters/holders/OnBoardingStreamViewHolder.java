package com.shootr.mobile.ui.adapters.holders;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.amulyakhare.textdrawable.TextDrawable;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnBoardingFavoriteClickListener;
import com.shootr.mobile.ui.model.OnBoardingModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.widgets.FollowButton;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;

public class OnBoardingStreamViewHolder extends RecyclerView.ViewHolder {

  private final OnBoardingFavoriteClickListener onFavoriteClickListener;
  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;

  @BindView(R.id.stream_picture) ImageView picture;
  @BindView(R.id.stream_picture_without_text) ImageView pictureWithoutText;
  @BindView(R.id.stream_title) TextView title;
  @BindView(R.id.favorite_stream_indicator) ShineButton favorite;
  @BindView(R.id.stream_verified) ImageView streamVerified;
  @Nullable @BindView(R.id.stream_remove) ImageView removeButton;
  @Nullable @BindView(R.id.stream_subtitle) TextView subtitle;
  @Nullable @BindView(R.id.stream_subtitle_description) TextView subtitleDescription;
  @Nullable @BindView(R.id.stream_actions_container) View actionsContainer;
  @BindView(R.id.stream_rank) TextView rankNumber;
  @BindView(R.id.user_follow_button) FollowButton followButton;


  public OnBoardingStreamViewHolder(View itemView,
      OnBoardingFavoriteClickListener onFavoriteClickListener, ImageLoader imageLoader,
      InitialsLoader initialsLoader) {
    super(itemView);
    this.onFavoriteClickListener = onFavoriteClickListener;
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    ButterKnife.bind(this, itemView);
  }

  public void render(OnBoardingModel onBoardingStreamModel) {
    this.setupFavoriteClickListener(onBoardingStreamModel);
    title.setText(onBoardingStreamModel.getStreamModel().getTitle());
    renderSubtitle(onBoardingStreamModel.getStreamModel());
    handleFavorite(onBoardingStreamModel);
    setupStreamPicture(onBoardingStreamModel.getStreamModel());
    rankNumber.setVisibility(View.GONE);
  }

  private void setVerifiedVisibility(StreamModel streamModel) {
    if (streamModel.isVerifiedUser()) {
      streamVerified.setVisibility(View.VISIBLE);
    } else {
      streamVerified.setVisibility(View.GONE);
    }
  }

  private void handleFavorite(OnBoardingModel onBoardingStreamModel) {
    if (onBoardingStreamModel.isFavorite()) {
      showIsFavorite();
    } else {
      showIsNotFavorite();
    }
  }

  private void showIsFavorite() {
    followButton.setVisibility(View.VISIBLE);
    followButton.setFollowing(true);
  }

  private void showIsNotFavorite() {
    followButton.setVisibility(View.VISIBLE);
    followButton.setFollowing(false);
  }

  private void setupStreamPicture(StreamModel streamModel) {
    String pictureUrl = streamModel.getPicture();
    if (pictureUrl != null) {
      picture.setVisibility(View.VISIBLE);
      pictureWithoutText.setVisibility(View.GONE);
      imageLoader.loadStreamPicture(pictureUrl, picture);
    } else {
      picture.setVisibility(View.GONE);
      pictureWithoutText.setVisibility(View.VISIBLE);
      setupInitials(streamModel);
    }
  }

  private void setupInitials(StreamModel streamModel) {
    String initials = initialsLoader.getLetters(streamModel.getTitle());
    int backgroundColor = initialsLoader.getColorForLetters(initials);
    TextDrawable textDrawable = initialsLoader.getTextDrawable(initials, backgroundColor);
    pictureWithoutText.setImageDrawable(textDrawable);
  }

  private void renderSubtitle(StreamModel stream) {
    setupAuthorAndDescriptionSubtitle(stream);
    setVerifiedVisibility(stream);
  }

  private void setupAuthorAndDescriptionSubtitle(StreamModel stream) {
    if (subtitle != null && subtitleDescription != null) {
      subtitle.setText(stream.getAuthorUsername());
      if (stream.getDescription() != null) {
        subtitleDescription.setVisibility(View.VISIBLE);
        subtitleDescription.setText(stream.getDescription());
      } else {
        subtitleDescription.setVisibility(View.GONE);
      }
    }
  }

  private void setupFavoriteClickListener(final OnBoardingModel onBoardingStreamModel) {
    if (onFavoriteClickListener != null) {
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          handleFavoriteStatus(onBoardingStreamModel);
        }
      });
      followButton.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          handleFavoriteStatus(onBoardingStreamModel);
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
