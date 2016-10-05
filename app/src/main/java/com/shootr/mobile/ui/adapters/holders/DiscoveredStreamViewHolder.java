package com.shootr.mobile.ui.adapters.holders;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnDiscoveredFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnDiscoveredStreamClickListener;
import com.shootr.mobile.ui.model.DiscoveredModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.util.ImageLoader;
import com.yayandroid.parallaxrecyclerview.ParallaxImageView;
import com.yayandroid.parallaxrecyclerview.ParallaxViewHolder;

public class DiscoveredStreamViewHolder extends ParallaxViewHolder {

  @BindView(R.id.container) FrameLayout container;
  @BindView(R.id.stream_image) ParallaxImageView streamImage;
  @BindView(R.id.stream_title) TextView streamTitle;
  @BindView(R.id.stream_description) TextView streamDescription;
  @BindView(R.id.favorite_discovered_indicator) ShineButton favoriteButton;

  private final ImageLoader imageLoader;
  private final OnDiscoveredStreamClickListener onDiscoveredStreamClickListener;
  private final OnDiscoveredFavoriteClickListener onFavoriteClickListener;

  public DiscoveredStreamViewHolder(View itemView, ImageLoader imageLoader,
      OnDiscoveredStreamClickListener onDiscoveredStreamClickListener,
      OnDiscoveredFavoriteClickListener onFavoriteClickListener) {
    super(itemView);
    this.onFavoriteClickListener = onFavoriteClickListener;
    ButterKnife.bind(this, itemView);
    this.imageLoader = imageLoader;
    this.onDiscoveredStreamClickListener = onDiscoveredStreamClickListener;
  }

  public void render(final DiscoveredModel discoveredModel,  Boolean landscape) {
    final StreamModel streamModel = discoveredModel.getStreamModel();
    setupInfo(streamModel, landscape);
    container.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onDiscoveredStreamClickListener.onStreamClick(streamModel.getIdStream());
      }
    });
    setupFavoriteButton(discoveredModel);
  }

  private void setupInfo(StreamModel streamModel, boolean landscape) {
    setupImage(streamModel, landscape);
    streamImage.reuse();
    streamTitle.setText(streamModel.getTitle());
    streamDescription.setText(streamModel.getDescription());
  }

  private void setupImage(StreamModel streamModel, boolean landscape) {
    if (landscape) {
      imageLoader.loadDiscoverImage(
          streamModel.getLandscapePicture() != null ? streamModel.getLandscapePicture()
              : streamModel.getPicture(), streamImage, new ImageLoader.Callback() {
            @Override public void onLoaded() {
              streamImage.centerCrop(true);
            }
          });
    } else {
      imageLoader.loadDiscoverImage(streamModel.getPicture(), streamImage,
          new ImageLoader.Callback() {
            @Override public void onLoaded() {
              streamImage.centerCrop(true);
            }
          });
    }
  }

  private void setupFavoriteButton(final DiscoveredModel discoveredModel) {
    favoriteButton.setChecked(discoveredModel.getHasBeenFaved());
    favoriteButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (((ShineButton) view).isChecked()) {
          onFavoriteClickListener.onFavoriteClick(discoveredModel.getStreamModel().getIdStream());
        } else {
          onFavoriteClickListener.onRemoveFavoriteClick(discoveredModel.getStreamModel().getIdStream());
        }
      }
    });
  }

  @Override public int getParallaxImageId() {
    return R.id.stream_image;
  }
}
