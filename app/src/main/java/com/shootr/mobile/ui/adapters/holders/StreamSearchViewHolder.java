package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.amulyakhare.textdrawable.TextDrawable;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.FavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnSearchStreamClickListener;
import com.shootr.mobile.ui.model.StreamModel;
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
  @BindView(R.id.favorite_stream_indicator) ShineButton favorite;
  @BindView(R.id.stream_verified) ImageView streamVerified;
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
    title.setText(streamModel.getTitle());
    renderSubtitle(streamModel);
    handleShowFavorite(streamModel);
    setupStreamPicture(streamModel);
    rankNumber.setVisibility(View.GONE);
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

  private void setVerifiedVisibility(StreamModel streamModel) {
    if (streamModel.isVerifiedUser()) {
      streamVerified.setVisibility(View.VISIBLE);
    } else {
      streamVerified.setVisibility(View.GONE);
    }
  }

  private void setupFavoriteClickListener(final StreamModel streamResult) {
    if (onFavoriteClickListener != null) {
      favorite.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (streamResult.isFavorite()) {
            onFavoriteClickListener.onRemoveFavoriteClick(streamResult);
          } else {
            onFavoriteClickListener.onFavoriteClick(streamResult);
          }
        }
      });
    }
  }

  private void handleShowFavorite(StreamModel streamResultModel) {
    favorite.setVisibility(View.VISIBLE);
    favorite.setChecked(streamResultModel.isFavorite());
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
