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
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.Truss;
import java.text.DecimalFormat;

public class StreamViewHolder extends BaseViewHolder<StreamModel> {

  private final OnLandingStreamClickListener onStreamClickListener;
  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;
  private final NumberFormatUtil numberFormatUtil;

  private boolean isWatchingStateEnabled = false;

  @BindView(R.id.stream_picture) ImageView picture;
  @BindView(R.id.stream_picture_without_text) ImageView pictureWithoutText;
  @BindView(R.id.stream_title) TextView title;
  @BindView(R.id.stream_muted) ImageView mute;
  @BindView(R.id.favorite_stream_indicator) ShineButton favorite;
  @BindView(R.id.stream_verified) ImageView streamVerified;
  @BindView(R.id.stream_rank) TextView rankNumber;
  @BindView(R.id.stream_badge) TextView streamBadge;
  @Nullable @BindView(R.id.stream_remove) ImageView removeButton;
  @Nullable @BindView(R.id.stream_subtitle) TextView subtitle;
  @Nullable @BindView(R.id.stream_subtitle_description) TextView subtitleDescription;
  @Nullable @BindView(R.id.stream_actions_container) View actionsContainer;

  @BindString(R.string.watching_stream_connected) String connected;
  @BindString(R.string.watching_stream_connected_muted) String connectedAndMuted;
  DecimalFormat formatter;

  public StreamViewHolder(View itemView, OnLandingStreamClickListener onStreamClickListener,
      ImageLoader imageLoader, InitialsLoader initialsLoader, NumberFormatUtil numberFormatUtil) {
    super(itemView);
    this.numberFormatUtil = numberFormatUtil;
    formatter = new DecimalFormat("#,###,###");
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
    handleBadgeVisibility(streamModel);
  }

  private void handleBadgeVisibility(StreamModel streamModel) {
    if (streamModel.shouldShowBadge()) {
      streamBadge.setVisibility(View.VISIBLE);
    } else {
      streamBadge.setVisibility(View.INVISIBLE);
    }
  }

  private void handleShowRankNumber(StreamModel streamModel) {
    if (streamModel.isShowRankPosition()) {
      rankNumber.setText(String.valueOf(streamModel.getPosition()));
      rankNumber.setVisibility(View.VISIBLE);
    } else {
      rankNumber.setVisibility(View.INVISIBLE);
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

  private void setMutedVisibility(StreamModel streamModel) {
    if (streamModel.isMuted()) {
      mute.setVisibility(View.VISIBLE);
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
              formatter.format(stream.getTotalFollowers()));
      subtitleDescription.setText(favorites);
    }
  }

  private void renderHolderOwnSubtitle(StreamModel stream) {
    if (subtitle != null) {
      subtitleDescription.setVisibility(View.VISIBLE);
      String numViews = subtitle.getContext()
          .getResources()
          .getQuantityString(R.plurals.view_count_pattern, ((int) stream.getViews()),
              numberFormatUtil.formatNumbers(stream.getViews()));
      subtitleDescription.setText(numViews);
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
