package com.shootr.mobile.ui.adapters.holders;

import android.support.annotation.Nullable;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ahamed.multiviewadapter.BaseViewHolder;
import com.amulyakhare.textdrawable.TextDrawable;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnLandingStreamClickListener;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.widgets.FollowButton;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.Truss;

public class StreamViewHolder extends BaseViewHolder<StreamModel> {

  private final OnLandingStreamClickListener onStreamClickListener;
  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;

  private boolean showsFavoritesText = false;
  private boolean isWatchingStateEnabled = false;

  @BindView(R.id.stream_picture) ImageView picture;
  @BindView(R.id.stream_picture_without_text) ImageView pictureWithoutText;
  @BindView(R.id.stream_title) TextView title;
  @BindView(R.id.stream_muted) ImageView mute;
  @BindView(R.id.favorite_stream_indicator) ShineButton favorite;
  @BindView(R.id.stream_verified) ImageView streamVerified;
  @BindView(R.id.stream_rank) TextView rankNumber;
  @Nullable @BindView(R.id.stream_remove) ImageView removeButton;
  @Nullable @BindView(R.id.stream_subtitle) TextView subtitle;
  @Nullable @BindView(R.id.stream_subtitle_description) TextView subtitleDescription;
  @Nullable @BindView(R.id.stream_actions_container) View actionsContainer;
  @BindView(R.id.user_follow_button) FollowButton followButton;

  @BindString(R.string.watching_stream_connected) String connected;
  @BindString(R.string.watching_stream_connected_muted) String connectedAndMuted;

  public StreamViewHolder(View itemView, OnLandingStreamClickListener onStreamClickListener, ImageLoader imageLoader,
      InitialsLoader initialsLoader) {
    super(itemView);
    this.onStreamClickListener = onStreamClickListener;
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    ButterKnife.bind(this, itemView);
  }

  public void render(StreamModel streamModel) {
    this.setClickListener(streamModel);
    title.setText(streamModel.getTitle());
    setMutedVisibility(streamModel);
    renderSubtitle(streamModel);
    renderHolderOwnSubtitle(streamModel);
    handleShowRankNumber(streamModel);
    handleShowFavorite();
    setupStreamPicture(streamModel);
  }


  private void handleShowRankNumber(StreamModel streamModel) {
    if (streamModel.isShowRankPosition()) {
        rankNumber.setText(streamModel.getPosition());
    } else {
      rankNumber.setVisibility(View.GONE);
    }
  }

  private void setVerifiedVisibility(StreamModel streamModel) {
    if (streamModel.isVerifiedUser()) {
      streamVerified.setVisibility(View.VISIBLE);
    } else {
      streamVerified.setVisibility(View.GONE);
    }
  }

  private void handleShowFavorite() {
    favorite.setVisibility(View.GONE);
  }

  private void showIsFavorite() {
    followButton.setVisibility(View.VISIBLE);
    followButton.setFollowing(true);
  }

  private void showIsNotFavorite() {
    followButton.setVisibility(View.VISIBLE);
    followButton.setFollowing(false);
  }

  private void setupStreamPicture(StreamModel streamResultModel) {
    String pictureUrl = streamResultModel.getPicture();
    if (pictureUrl != null) {
      picture.setVisibility(View.VISIBLE);
      pictureWithoutText.setVisibility(View.GONE);
      imageLoader.loadStreamPicture(pictureUrl, picture);
    } else {
      picture.setVisibility(View.GONE);
      pictureWithoutText.setVisibility(View.VISIBLE);
      setupInitials(streamResultModel);
    }
  }

  private void setupInitials(StreamModel streamResultModel) {
    String initials = initialsLoader.getLetters(streamResultModel.getTitle());
    int backgroundColor = initialsLoader.getColorForLetters(initials);
    TextDrawable textDrawable = initialsLoader.getTextDrawable(initials, backgroundColor);
    pictureWithoutText.setImageDrawable(textDrawable);
  }

  public void setMutedVisibility(StreamModel streamModel) {
    if (!isWatchingStateEnabled) {
      if (streamModel.isMuted()) {
        mute.setVisibility(View.VISIBLE);
      } else {
        mute.setVisibility(View.GONE);
      }
    } else {
      mute.setVisibility(View.GONE);
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
        return onStreamClickListener.onStreamLongClick(streamResult);
      }
    });
  }

  protected void renderSubtitle(StreamModel stream) {
    if (isWatchingStateEnabled) {
      setupConnectedSubtitle(stream);
    } else {
      setupAuthorAndDescriptionSubtitle(stream);
      setVerifiedVisibility(stream);
    }
  }

  private void setupConnectedSubtitle(StreamModel stream) {
    if (subtitle != null && subtitleDescription != null) {
      subtitle.setText(getConnectedSubtitle(stream));
      subtitleDescription.setVisibility(View.GONE);
    }
  }

  private void setupAuthorAndDescriptionSubtitle(StreamModel stream) {
    if (subtitle != null && subtitleDescription != null) {
      subtitle.setText("@" + stream.getAuthorUsername());
      subtitleDescription.setVisibility(View.VISIBLE);
      String favorites = subtitle.getContext()
          .getResources()
          .getQuantityString(R.plurals.listing_favorites, stream.getTotalFollowers(),
              stream.getTotalFollowers());
      subtitleDescription.setText(favorites);
    }
  }

  private void renderHolderOwnSubtitle(StreamModel stream) {
    if (subtitle != null) {
      subtitleDescription.setVisibility(View.VISIBLE);
      String favorites = subtitle.getContext()
          .getResources()
          .getQuantityString(R.plurals.listing_favorites,
              stream.getTotalFollowers(),
              stream.getTotalFollowers());
      subtitleDescription.setText(favorites);
    }
  }

  private void renderHolderSubtitle(StreamResultModel stream) {
    if (subtitle != null) {
      if (isWatchingStateEnabled) {
        subtitle.setText(getConnectedSubtitle(stream.getStreamModel()));
      } else {
        String favorites = subtitle.getContext()
            .getResources()
            .getQuantityString(R.plurals.listing_favorites,
                stream.getStreamModel().getTotalFollowers(),
                stream.getStreamModel().getTotalFollowers());
        subtitle.setText(favorites);
      }
    }
  }

  private CharSequence getConnectedSubtitle(StreamModel stream) {
    if (stream.isMuted()) {
      return new Truss().pushSpan(
          new TextAppearanceSpan(itemView.getContext(), R.style.InlineConnectedAppearance))
          .append(connectedAndMuted)
          .popSpan()
          .build();
    } else {
      return new Truss().pushSpan(
          new TextAppearanceSpan(itemView.getContext(), R.style.InlineConnectedAppearance))
          .append(connected)
          .popSpan()
          .build();
    }
  }
}
