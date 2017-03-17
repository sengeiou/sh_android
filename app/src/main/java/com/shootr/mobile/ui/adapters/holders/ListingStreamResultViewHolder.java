package com.shootr.mobile.ui.adapters.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import java.util.List;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class ListingStreamResultViewHolder extends StreamResultViewHolder {

  public static final int FAVORITE_ADDED_IMAGE = R.drawable.ic_favorite_added_28_gray50;
  public static final int FAVORITE_NOT_ADDED_IMAGE = R.drawable.ic_favorite_not_added_28_gray50;

  private final OnFavoriteClickListener onFavoriteClickListener;

  @BindView(R.id.favorite_stream_indicator) ImageView favoriteIndicator;
  @BindView(R.id.stream_rank) TextView rankNumber;
  private Boolean isFavorite;

  public ListingStreamResultViewHolder(View itemView, OnStreamClickListener onStreamClickListener,
      ImageLoader imageLoader, OnFavoriteClickListener onFavoriteClickListener,
      InitialsLoader initialsLoader) {
    super(itemView, onStreamClickListener, onFavoriteClickListener, imageLoader, initialsLoader);
    this.onFavoriteClickListener = onFavoriteClickListener;
  }

  @Override public void render(StreamResultModel streamResultModel, boolean hasToShowIsFavorite,
      Integer position, boolean hasToShowRankNumber) {
    super.render(streamResultModel, hasToShowIsFavorite, position, hasToShowRankNumber);
    setupFavoriteIndicator(streamResultModel);
  }

  @Override
  public void render(StreamResultModel streamResultModel, List<StreamResultModel> favoritedStreams,
      boolean hasToShowIsFavorite, Integer position, boolean hasToShowRankNumber) {
    super.render(streamResultModel, favoritedStreams, hasToShowIsFavorite, position,
        hasToShowRankNumber);
    setupFavoriteIndicator(streamResultModel);
  }

  private void setupFavoriteIndicator(StreamResultModel streamResultModel) {
    checkNotNull(isFavorite, "Should call setFavorite(boolean) before calling render()");
    favoriteIndicator.setVisibility(View.VISIBLE);
    setupFavoriteClickListener(streamResultModel);
    updateIndicatorStatus();
  }

  public void setFavorite(boolean isFavorite) {
    this.isFavorite = isFavorite;
  }

  private void updateIndicatorStatus() {
    if (isFavorite) {
      showIsFavorite();
    } else {
      showIsNotFavorite();
    }
  }

  private void showIsFavorite() {
    favoriteIndicator.setImageResource(FAVORITE_ADDED_IMAGE);
  }

  private void showIsNotFavorite() {
    favoriteIndicator.setImageResource(FAVORITE_NOT_ADDED_IMAGE);
  }

  private void setupFavoriteClickListener(final StreamResultModel streamResult) {
    favoriteIndicator.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (isFavorite) {
          onFavoriteClickListener.onRemoveFavoriteClick(streamResult);
        } else {
          onFavoriteClickListener.onFavoriteClick(streamResult);
        }
        isFavorite = !isFavorite;
        updateIndicatorStatus();
      }
    });
  }
}
