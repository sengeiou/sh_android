package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.amulyakhare.textdrawable.TextDrawable;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.FavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnSearchStreamClickListener;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.widgets.FollowButton;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;

public class StreamSearchViewHolder extends RecyclerView.ViewHolder {

  private final OnSearchStreamClickListener onStreamClickListener;
  private final FavoriteClickListener onFavoriteClickListener;
  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;

  @BindView(R.id.stream_picture) ImageView picture;
  @BindView(R.id.stream_picture_without_text) ImageView pictureWithoutText;
  @BindView(R.id.stream_title) TextView title;
  @BindView(R.id.stream_muted) ImageView mute;
  @BindView(R.id.user_follow_button) FollowButton followButton;
  @BindView(R.id.stream_rank) TextView rankNumber;
  @BindView(R.id.stream_remove) ImageView removeButton;
  @BindView(R.id.stream_subtitle) TextView subtitle;
  @BindView(R.id.stream_subtitle_description) TextView subtitleDescription;
  @BindView(R.id.stream_actions_container) View actionsContainer;

  public StreamSearchViewHolder(View itemView, OnSearchStreamClickListener onStreamClickListener,
      FavoriteClickListener onFavoriteClickListener, ImageLoader imageLoader,
      InitialsLoader initialsLoader) {
    super(itemView);
    this.onStreamClickListener = onStreamClickListener;
    this.onFavoriteClickListener = onFavoriteClickListener;
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    ButterKnife.bind(this, itemView);
  }

  public void render(StreamModel streamModel) {
    this.setClickListener(streamModel);
    this.setupFavoriteClickListener(streamModel);
    setTitle(streamModel);
    renderSubtitle(streamModel);
    handleShowFavorite(streamModel);
    setupStreamPicture(streamModel);
    rankNumber.setVisibility(View.GONE);
  }

  private void setTitle(StreamModel streamModel) {
    title.setText(streamModel.getTitle());
    if (streamModel.isVerifiedUser()) {
      title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_verified_user_list,
          0);
      title.setCompoundDrawablePadding(6);
    } else {
      title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }
  }

  private void setClickListener(final StreamModel streamResult) {
    itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        onStreamClickListener.onStreamClick(streamResult);
      }
    });
    itemView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View v) {
        onStreamClickListener.onStreamLongClick(streamResult);
        return false;
      }
    });
  }

  private void renderSubtitle(StreamModel stream) {
    setupAuthorAndDescriptionSubtitle(stream);
  }

  private void setupAuthorAndDescriptionSubtitle(StreamModel stream) {
    if (subtitle != null && subtitleDescription != null) {
      subtitle.setText("@" + stream.getAuthorUsername());
      subtitleDescription.setVisibility(View.VISIBLE);
      String favorites = subtitle.getContext()
          .getResources()
          .getQuantityString(R.plurals.listing_favorites, stream.getTotalFavorites(),
              stream.getTotalFavorites());
      subtitleDescription.setText(favorites);
    }
  }

  private void setupFavoriteClickListener(final StreamModel streamResult) {
    if (onFavoriteClickListener != null) {
      followButton.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (streamResult.isFavorite()) {
            onFavoriteClickListener.onRemoveFavoriteClick(streamResult);
          } else {
            onFavoriteClickListener.onFavoriteClick(streamResult);
          }
          followButton.setFollowing(streamResult.isFavorite());
        }
      });
    }
  }

  private void handleShowFavorite(StreamModel streamResultModel) {
    followButton.setVisibility(View.VISIBLE);
    followButton.setFollowing(streamResultModel.isFavorite());
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
}
