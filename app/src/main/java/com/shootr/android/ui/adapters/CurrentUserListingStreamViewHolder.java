package com.shootr.android.ui.adapters;

import android.view.View;
import android.widget.ImageView;
import butterknife.Bind;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnFavoriteClickListener;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.util.PicassoWrapper;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class CurrentUserListingStreamViewHolder extends CurrentUserStreamViewHolder {

    public static final int FAVORITE_ADDED_IMAGE = R.drawable.ic_favorite_added_28_gray50;
    public static final int FAVORITE_NOT_ADDED_IMAGE = R.drawable.ic_favorite_not_added_28_gray50;

    private final OnFavoriteClickListener onFavoriteClickListener;

    @Bind(R.id.favorite_stream_indicator) ImageView favoriteIndicator;
    private Boolean isFavorite;

    public CurrentUserListingStreamViewHolder(View itemView, OnStreamClickListener onStreamClickListener,
      PicassoWrapper picasso, OnFavoriteClickListener onFavoriteClickListener) {
        super(itemView, onStreamClickListener, picasso);
        this.onFavoriteClickListener = onFavoriteClickListener;
    }

    @Override
    public void render(StreamResultModel streamResultModel, boolean showSeparator) {
        super.render(streamResultModel, showSeparator);
        checkNotNull(isFavorite, "Should call setFavorite(boolean) before calling render()");
        favoriteIndicator.setVisibility(View.VISIBLE);
        setupFavoriteClickListener(streamResultModel);
        updateIndicatorStatus();
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    private void updateIndicatorStatus() {
        if(isFavorite) {
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
            @Override
            public void onClick(View view) {
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
