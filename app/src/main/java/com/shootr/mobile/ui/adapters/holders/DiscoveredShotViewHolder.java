package com.shootr.mobile.ui.adapters.holders;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnDiscoveredFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnDiscoveredShotClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnDiscoveredStreamClickListener;
import com.shootr.mobile.ui.model.DiscoveredModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.yayandroid.parallaxrecyclerview.ParallaxImageView;
import com.yayandroid.parallaxrecyclerview.ParallaxViewHolder;
import de.hdodenhof.circleimageview.CircleImageView;

public class DiscoveredShotViewHolder extends ParallaxViewHolder {

  private static final float ALPHA = 0.7f;

  @BindView(R.id.container) FrameLayout container;
  @BindView(R.id.shot_container) RelativeLayout shotContainer;
  @BindView(R.id.shot_image) ParallaxImageView shotImage;
  @BindView(R.id.shot_avatar) CircleImageView avatar;
  @BindView(R.id.shot_username) TextView shotUsername;
  @BindView(R.id.shot_comment) TextView shotComment;
  @BindView(R.id.nice_discovered_indicator) ShineButton favoriteButton;
  @BindView(R.id.shot_timestamp) TextView timestamp;
  @BindView(R.id.stream_title) TextView streamTitle;

  private final ImageLoader imageLoader;
  private final OnDiscoveredShotClickListener onDiscoveredShotClickListener;
  private final OnAvatarClickListener onAvatarClickListener;
  private final OnDiscoveredFavoriteClickListener onFavoriteClickListener;
  private final OnDiscoveredStreamClickListener onDiscoveredStreamClickListener;
  private final AndroidTimeUtils timeUtils;

  public DiscoveredShotViewHolder(View itemView, ImageLoader imageLoader,
      OnDiscoveredShotClickListener onDiscoveredShotClickListener,
      OnAvatarClickListener onAvatarClickListener,
      OnDiscoveredFavoriteClickListener onFavoriteClickListener,
      OnDiscoveredStreamClickListener onDiscoveredStreamClickListener, AndroidTimeUtils timeUtils) {
    super(itemView);
    this.onAvatarClickListener = onAvatarClickListener;
    this.onFavoriteClickListener = onFavoriteClickListener;
    this.onDiscoveredStreamClickListener = onDiscoveredStreamClickListener;
    this.timeUtils = timeUtils;
    ButterKnife.bind(this, itemView);
    this.imageLoader = imageLoader;
    this.onDiscoveredShotClickListener = onDiscoveredShotClickListener;
  }

  public void render(final DiscoveredModel discoveredModel) {
    final ShotModel shotModel = discoveredModel.getShotModel();
    setupInfo(shotModel);
    container.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onDiscoveredStreamClickListener.onStreamClick(shotModel.getStreamId());
      }
    }); shotContainer.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onDiscoveredShotClickListener.onShotClick(shotModel);
      }
    });
    setupFavoriteButton(discoveredModel);
  }

  private void setupInfo(ShotModel shotModel) {
    imageLoader.loadDiscoverImage(shotModel.getImage().getImageUrl(), shotImage,
        new ImageLoader.Callback() {
          @Override public void onLoaded() {
            shotImage.centerCrop(true);
          }
        });
    shotImage.reuse();
    setupAvatarPicture(shotModel);
    setupTimestamp(shotModel);
    shotUsername.setText(shotModel.getUsername());
    setupShotComment(shotModel);
    streamTitle.setText(shotModel.getStreamTitle());
  }

  private void setupShotComment(ShotModel shotModel) {
    if (shotModel.getComment() == null || shotModel.getComment().isEmpty()) {
      shotComment.setVisibility(View.GONE);
    } else {
      shotComment.setVisibility(View.VISIBLE);
      shotComment.setText(shotModel.getComment());
    }
  }

  private void setupTimestamp(ShotModel shotModel) {
    long shotTimestamp = shotModel.getBirth().getTime();
    String time = itemView.getResources()
        .getString(R.string.discover_timestamp,
            timeUtils.getElapsedTime(timestamp.getContext(), shotTimestamp));
    timestamp.setText(time);
  }

  private void setupAvatarPicture(final ShotModel shotModel) {
    imageLoader.loadProfilePhoto(shotModel.getAvatar(), avatar);
    setupAvatarAlpha(shotModel);
    avatar.setVisibility(View.VISIBLE);
    avatar.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onAvatarClickListener.onAvatarClick(shotModel.getIdUser(), view);
      }
    });
  }

  private void setupAvatarAlpha(ShotModel shotModel) {
    if (shotModel.getAvatar() == null || shotModel.getAvatar().isEmpty()) {
      avatar.setAlpha(ALPHA);
    }
  }

  private void setupFavoriteButton(final DiscoveredModel discoveredModel) {
    favoriteButton.setChecked(discoveredModel.getHasBeenFaved());
    favoriteButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (((ShineButton) view).isChecked()) {
          onFavoriteClickListener.onFavoriteClick(discoveredModel.getShotModel().getStreamId(),
              discoveredModel.getShotModel().getStreamTitle());
        } else {
          onFavoriteClickListener.onRemoveFavoriteClick(
              discoveredModel.getShotModel().getStreamId());
        }
      }
    });
  }

  @Override public int getParallaxImageId() {
    return R.id.shot_image;
  }
}
