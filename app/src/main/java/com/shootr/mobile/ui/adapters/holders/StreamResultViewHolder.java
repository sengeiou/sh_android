package com.shootr.mobile.ui.adapters.holders;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.amulyakhare.textdrawable.TextDrawable;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUnwatchClickListener;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.widgets.FollowButton;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.Truss;
import java.util.List;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class StreamResultViewHolder extends RecyclerView.ViewHolder {

  private final OnStreamClickListener onStreamClickListener;
  private final OnFavoriteClickListener onFavoriteClickListener;
  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;
  private OnUnwatchClickListener unwatchClickListener;
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

  public StreamResultViewHolder(View itemView, OnStreamClickListener onStreamClickListener,
      OnFavoriteClickListener onFavoriteClickListener, ImageLoader imageLoader,
      InitialsLoader initialsLoader) {
    super(itemView);
    this.onStreamClickListener = onStreamClickListener;
    this.onFavoriteClickListener = onFavoriteClickListener;
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    ButterKnife.bind(this, itemView);
  }

  public void enableWatchingState(OnUnwatchClickListener unwatchClickListener) {
    checkNotNull(removeButton,
        "The view used in this ViewHolder doesn't contain the unwatch button.");
    checkNotNull(unwatchClickListener);
    this.isWatchingStateEnabled = true;
    this.unwatchClickListener = unwatchClickListener;
    this.removeButton.setVisibility(View.VISIBLE);
    this.setUnwatchClickListener();
  }

  public void render(StreamResultModel streamResultModel, boolean hasToShowIsFavorite,
      Integer position, boolean hasToShowRankNumber) {
    this.setClickListener(streamResultModel);
    this.setupFavoriteClickListener(streamResultModel);
    title.setText(streamResultModel.getStreamModel().getTitle());
    setMutedVisibility(streamResultModel);
    renderSubtitle(streamResultModel.getStreamModel());
    handleShowRankNumber(position, hasToShowRankNumber);
    handleShowFavorite(streamResultModel, hasToShowIsFavorite);
    setupStreamPicture(streamResultModel);
  }

  private void handleShowRankNumber(Integer position, boolean hasToShowRankNumber) {
    if (hasToShowRankNumber) {
      position++;
      rankNumber.setText(position.toString());
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

  public void render(StreamResultModel streamResultModel, List<StreamResultModel> favoritedStreams,
      boolean hasToShowIsFavorite, Integer position, boolean hasToShowRankNumber,
      boolean hasToShowFollowers) {
    this.setClickListener(streamResultModel);
    this.setupFavoriteClickListener(streamResultModel);
    title.setText(streamResultModel.getStreamModel().getTitle());
    setMutedVisibility(streamResultModel);
    renderSubtitle(streamResultModel.getStreamModel());
    if ((showsFavoritesText && !favoritedStreams.contains(streamResultModel))
        || hasToShowFollowers) {
      if (hasToShowFollowers) {
        renderHolderOwnSubtitle(streamResultModel);
      } else {
        renderHolderSubtitle(streamResultModel);
      }
    }
    handleShowRankNumber(position, hasToShowRankNumber);
    handleShowFavorite(streamResultModel, hasToShowIsFavorite);
    setupStreamPicture(streamResultModel);
  }

  private void handleShowFavorite(StreamResultModel streamResultModel,
      boolean hasToShowIsFavorite) {
    if (hasToShowIsFavorite) {
      if (streamResultModel.isFavorited() && !isWatchingStateEnabled) {
        showIsFavorite();
      } else if (isWatchingStateEnabled) {
        favorite.setVisibility(View.GONE);
      } else {
        favorite.setVisibility(View.VISIBLE);
        showIsNotFavorite();
      }
    } else {
      favorite.setVisibility(View.GONE);
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

  private void setupStreamPicture(StreamResultModel streamResultModel) {
    String pictureUrl = streamResultModel.getStreamModel().getPicture();
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

  private void setupInitials(StreamResultModel streamResultModel) {
    String initials = initialsLoader.getLetters(streamResultModel.getStreamModel().getTitle());
    int backgroundColor = initialsLoader.getColorForLetters(initials);
    TextDrawable textDrawable = initialsLoader.getTextDrawable(initials, backgroundColor);
    pictureWithoutText.setImageDrawable(textDrawable);
  }

  public void setMutedVisibility(StreamResultModel streamResultModel) {
    if (!isWatchingStateEnabled) {
      if (streamResultModel.getStreamModel().isMuted()) {
        mute.setVisibility(View.VISIBLE);
      } else {
        mute.setVisibility(View.GONE);
      }
    } else {
      mute.setVisibility(View.GONE);
    }
  }

  private void setClickListener(final StreamResultModel streamResult) {
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

  private void setUnwatchClickListener() {
    checkNotNull(actionsContainer,
        "The view used in this ViewHolder doesn't contain the unwatch button.");
    actionsContainer.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (unwatchClickListener != null) {
          unwatchClickListener.onUnwatchClick();
        }
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
          .getQuantityString(R.plurals.listing_favorites, stream.getTotalFavorites(),
              stream.getTotalFavorites());
      subtitleDescription.setText(favorites);
    }
  }

  private void renderHolderOwnSubtitle(StreamResultModel stream) {
    if (subtitle != null) {
      subtitleDescription.setVisibility(View.VISIBLE);
      String favorites = subtitle.getContext()
          .getResources()
          .getQuantityString(R.plurals.listing_favorites,
              stream.getStreamModel().getTotalFavorites(),
              stream.getStreamModel().getTotalFavorites());
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
                stream.getStreamModel().getTotalFavorites(),
                stream.getStreamModel().getTotalFavorites());
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

  public void setShowsFavoritesText(Boolean showFavoritesText) {
    this.showsFavoritesText = showFavoritesText;
  }

  void setupFavoriteClickListener(final StreamResultModel streamResult) {
    if (onFavoriteClickListener != null) {
      followButton.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (streamResult.isFavorited()) {
            onFavoriteClickListener.onRemoveFavoriteClick(streamResult);
          } else {
            onFavoriteClickListener.onFavoriteClick(streamResult);
          }
        }
      });
    }
  }
}
