package com.shootr.android.ui.adapters;

import android.view.View;
import android.widget.ImageView;
import butterknife.Bind;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnFavoriteClickListener;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.util.PicassoWrapper;

public class ListingStreamResultViewHolder extends StreamResultViewHolder {

    public static final int FAVORITE_ADDED_IMAGE = R.drawable.ic_favorite_added_28_gray50;
    public static final int FAVORITE_NOT_ADDED_IMAGE = R.drawable.ic_favorite_not_added_28_gray50;
    private final OnFavoriteClickListener onFavoriteClickListener;

    @Bind(R.id.favorite_stream_indicator) ImageView favoriteIndicator;
    private boolean isFavorite;

    public ListingStreamResultViewHolder(View itemView, OnStreamClickListener onStreamClickListener,
      PicassoWrapper picasso, OnFavoriteClickListener onFavoriteClickListener) {
        super(itemView, onStreamClickListener, picasso);
        this.onFavoriteClickListener = onFavoriteClickListener;
    }

    public void render(StreamResultModel streamResultModel, Boolean isFavorite) {
        this.isFavorite = isFavorite;
        super.render(streamResultModel);
        setupFavoriteClickListener(streamResultModel);
        updateIndicatorStatus();
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
