package com.shootr.mobile.ui.adapters.holders;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
  @BindView(R.id.stream_static_image) ImageView streamStaticImage;
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

  public void render(final DiscoveredModel discoveredModel, boolean smallItem) {
    final StreamModel streamModel = discoveredModel.getStreamModel();
    setupInfo(streamModel, smallItem);
    container.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onDiscoveredStreamClickListener.onStreamClick(streamModel.getIdStream());
      }
    });
    setupFavoriteButton(discoveredModel);
  }

  private void setupInfo(StreamModel streamModel, boolean smallItem) {
    setupImage(streamModel, smallItem);
    streamImage.reuse();
    streamTitle.setText(streamModel.getTitle());
    streamDescription.setText(streamModel.getDescription());
  }

  private void setupImage(StreamModel streamModel, boolean smallItem) {
    setupImageVisibility(smallItem);
    imageLoader.loadDiscoverImage(streamModel.getPicture(), smallItem ? streamStaticImage : streamImage,
        new ImageLoader.Callback() {
          @Override public void onLoaded() {
            streamImage.centerCrop(true);
          }
        });
  }

  private void setupImageVisibility(boolean smallItem) {
    if (smallItem) {
      streamImage.setVisibility(View.GONE);
      streamStaticImage.setVisibility(View.VISIBLE);
    } else {
      streamImage.setVisibility(View.VISIBLE);
      streamStaticImage.setVisibility(View.GONE);
    }
  }

  private void setupFavoriteButton(final DiscoveredModel discoveredModel) {
    favoriteButton.setChecked(discoveredModel.getHasBeenFaved());
    favoriteButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (((ShineButton) view).isChecked()) {
          onFavoriteClickListener.onFavoriteClick(discoveredModel.getStreamModel().getIdStream(),
              discoveredModel.getStreamModel().getTitle());
        } else {
          onFavoriteClickListener.onRemoveFavoriteClick(
              discoveredModel.getStreamModel().getIdStream());
        }
      }
    });
  }

  @Override public int getParallaxImageId() {
    return R.id.stream_image;
  }
}
