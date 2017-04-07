package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.amulyakhare.textdrawable.TextDrawable;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnDiscoverTimelineFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnDiscoveredStreamClickListener;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import de.hdodenhof.circleimageview.CircleImageView;

public class DiscoverStreamViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.stream_title) TextView streamTitle;
  @BindView(R.id.stream_description) TextView streamDescription;
  @BindView(R.id.favorite_discover_indicator) ShineButton favoriteButton;
  @BindView(R.id.stream_picture) CircleImageView streamPicture;
  @BindView(R.id.stream_picture_without_image) ImageView pictureWithoutImage;
  @BindView(R.id.favorite_container) LinearLayout favoriteContainer;
  @BindView(R.id.stream_faved) TextView streamFaved;
  @BindView(R.id.stream_add_fav) TextView addToFavorites;

  private final OnDiscoveredStreamClickListener onDiscoveredStreamClickListener;
  private final OnDiscoverTimelineFavoriteClickListener onFavoriteClickListener;
  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;

  public DiscoverStreamViewHolder(View itemView, OnDiscoveredStreamClickListener onDiscoveredStreamClickListener,
      OnDiscoverTimelineFavoriteClickListener onFavoriteClickListener, ImageLoader imageLoader,
      InitialsLoader initialsLoader) {
    super(itemView);
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    ButterKnife.bind(this, itemView);
    this.onDiscoveredStreamClickListener = onDiscoveredStreamClickListener;
    this.onFavoriteClickListener = onFavoriteClickListener;
  }

  public void render(final StreamModel streamModel) {
    streamTitle.setText(streamModel.getTitle());
    itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onDiscoveredStreamClickListener.onStreamClick(streamModel.getIdStream());
      }
    });
    setupDescription(streamModel);
    setupFavoriteButton(streamModel);
    setupStreamPicture(streamModel);
  }

  private void setupDescription(StreamModel streamModel) {
    if (streamModel.getTotalFavorites() != null && streamModel.getTotalFavorites() > 0) {
      streamDescription.setText(streamModel.getTotalFavorites() + " favoritos");
      streamDescription.setVisibility(View.GONE);
    } else {
      streamDescription.setVisibility(View.GONE);
    }
  }

  private void setupStreamPicture(StreamModel streamModel) {
    String pictureUrl = streamModel.getPicture();
    if (pictureUrl != null) {
      streamPicture.setVisibility(View.VISIBLE);
      pictureWithoutImage.setVisibility(View.GONE);
      imageLoader.loadStreamPicture(pictureUrl, streamPicture);
    } else {
      streamPicture.setVisibility(View.GONE);
      pictureWithoutImage.setVisibility(View.VISIBLE);
      setupInitials(streamModel);
    }
  }

  private void setupInitials(StreamModel streamModel) {
    String initials = initialsLoader.getLetters(streamModel.getTitle());
    int backgroundColor = initialsLoader.getColorForLetters(initials);
    TextDrawable textDrawable = initialsLoader.getTextDrawable(initials, backgroundColor);
    pictureWithoutImage.setImageDrawable(textDrawable);
  }


  private void setupFavoriteButton(final StreamModel streamModel) {
    if (streamModel.isFavorite()) {
      addToFavorites.setVisibility(View.GONE);
      streamFaved.setVisibility(View.VISIBLE);
    } else {
      addToFavorites.setVisibility(View.VISIBLE);
      streamFaved.setVisibility(View.GONE);
    }
    favoriteButton.setChecked(streamModel.isFavorite());
    favoriteContainer.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (streamModel.isFavorite()) {
          onFavoriteClickListener.onRemoveFavoriteClick(
              streamModel);

        } else {
          onFavoriteClickListener.onFavoriteClick(streamModel);

        }
      }
    });

  }
}
