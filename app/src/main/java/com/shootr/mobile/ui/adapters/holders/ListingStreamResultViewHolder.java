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
import com.shootr.mobile.util.NumberFormatUtil;
import java.util.List;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class ListingStreamResultViewHolder extends StreamResultViewHolder {

  private final OnFavoriteClickListener onFavoriteClickListener;
  private final NumberFormatUtil numberFormatUtil;

  @BindView(R.id.favorite_stream_indicator) ImageView favoriteIndicator;
  @BindView(R.id.stream_rank) TextView rankNumber;
  private Boolean isFavorite;

  public ListingStreamResultViewHolder(View itemView, OnStreamClickListener onStreamClickListener,
      ImageLoader imageLoader, OnFavoriteClickListener onFavoriteClickListener,
      InitialsLoader initialsLoader, NumberFormatUtil numberFormatUtil) {
    super(itemView, onStreamClickListener, onFavoriteClickListener, imageLoader, initialsLoader,
        numberFormatUtil);
    this.onFavoriteClickListener = onFavoriteClickListener;
    this.numberFormatUtil = numberFormatUtil;
  }

  @Override public void render(StreamResultModel streamResultModel, boolean hasToShowIsFavorite,
      Integer position, boolean hasToShowRankNumber) {
    super.render(streamResultModel, hasToShowIsFavorite, position, hasToShowRankNumber);
    setupFavoriteIndicator(streamResultModel);
  }

  @Override
  public void render(StreamResultModel streamResultModel, List<StreamResultModel> favoritedStreams,
      boolean hasToShowIsFavorite, Integer position, boolean hasToShowRankNumber,
      boolean hasToShowFollowers) {
    super.render(streamResultModel, favoritedStreams, hasToShowIsFavorite, position,
        hasToShowRankNumber, hasToShowFollowers);
    setupFavoriteIndicator(streamResultModel);
  }

  private void setupFavoriteIndicator(StreamResultModel streamResultModel) {
    checkNotNull(isFavorite, "Should call setFollowing(boolean) before calling render()");
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
    followButton.setFollowing(true);
    followButton.setVisibility(View.VISIBLE);
  }

  private void showIsNotFavorite() {
    followButton.setFollowing(false);
    followButton.setVisibility(View.VISIBLE);
  }

  @Override void setupFavoriteClickListener(final StreamResultModel streamResult) {
    followButton.setOnClickListener(new View.OnClickListener() {
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
