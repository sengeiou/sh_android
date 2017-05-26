package com.shootr.mobile.ui.adapters.holders;

import android.support.annotation.NonNull;
import android.view.View;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.ActivityFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotClick;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;

public class StartedShootingViewHolder extends StreamActivityViewHolder {

    private final ActivityFavoriteClickListener activityFavoriteClickListener;

    public StartedShootingViewHolder(View view, ImageLoader imageLoader, AndroidTimeUtils androidTimeUtils,
        OnAvatarClickListener onAvatarClickListener, OnShotClick onShotClick,
        ActivityFavoriteClickListener activityFavoriteClickListener) {
        super(view, imageLoader, androidTimeUtils, onAvatarClickListener, onShotClick);
        this.activityFavoriteClickListener = activityFavoriteClickListener;
    }

    @NonNull protected String getCommentPattern() {
        return getContext().getString(R.string.started_shooting_activity_text_pattern);
    }

    @Override protected void renderFavorite(final ActivityModel activityModel) {
        if (activityModel.getShot().getImage() == null
            || activityModel.getShot().getImage().getImageUrl() == null) {
            image.setVisibility(View.GONE);
            favoriteButton.setVisibility(View.GONE);
            favoriteButton.setChecked(activityModel.isFavorite());
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    if (((ShineButton) view).isChecked()) {
                        activityFavoriteClickListener.onFavoriteClick(activityModel.getIdStream(),
                            activityModel.getStreamTitle());
                    } else {
                        activityFavoriteClickListener.onRemoveFavoriteClick(
                            activityModel.getIdStream());
                    }
                }
            });
        } else {
            image.setVisibility(View.VISIBLE);
            favoriteButton.setVisibility(View.GONE);
        }
    }

    @Override protected void renderImage(ActivityModel activity) {
        String shotImage = activity.getShot().getImage().getImageUrl();
        if (shotImage != null) {
            image.setVisibility(View.VISIBLE);
            imageLoader.load(shotImage, image);
        } else {
            image.setVisibility(View.GONE);
        }
    }

}
