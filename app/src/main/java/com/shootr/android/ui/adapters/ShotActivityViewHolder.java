package com.shootr.android.ui.adapters;

import android.text.style.ForegroundColorSpan;
import android.view.View;
import butterknife.BindColor;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnShotClick;
import com.shootr.android.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.ImageLoader;
import com.shootr.android.util.ShotTextSpannableBuilder;
import com.shootr.android.util.Truss;

public abstract class ShotActivityViewHolder extends GenericActivityViewHolder {

    private final ImageLoader imageLoader;
    private final ShotTextSpannableBuilder shotTextSpannableBuilder;
    private final OnUsernameClickListener onUsernameClickListener;
    private final OnShotClick onShotClickListener;

    @BindColor(R.color.gray_60) int shotCommentColor;

    public ShotActivityViewHolder(View view,
      ImageLoader imageLoader,
      AndroidTimeUtils androidTimeUtils,
      ShotTextSpannableBuilder shotTextSpannableBuilder,
      OnAvatarClickListener onAvatarClickListener,
      OnUsernameClickListener onUsernameClickListener, OnShotClick onShotClickListener) {
        super(view, imageLoader, androidTimeUtils, onAvatarClickListener);
        this.imageLoader = imageLoader;
        this.shotTextSpannableBuilder = shotTextSpannableBuilder;
        this.onUsernameClickListener = onUsernameClickListener;
        this.onShotClickListener = onShotClickListener;
    }

    @Override
    public void render(ActivityModel activity) {
        super.render(activity);
        enableShotClick(activity);
    }

    private void enableShotClick(final ActivityModel activity) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShotClickListener.onShotClick(activity.getShot());
            }
        });
    }

    @Override
    protected void renderText(ActivityModel activity) {
        CharSequence activityText = getActivityText(activity);
        shotTextSpannableBuilder.formatWithUsernameSpans(activityText, onUsernameClickListener);
        text.setText(activityText);
    }

    private CharSequence getActivityText(ActivityModel activity) {
        boolean hasCommentInShot = activity.getShot().getComment() == null;
        if (hasCommentInShot) {
            return buildActivityCommentWithShot(activity);
        } else {
            return getActivitySimpleComment(activity);
        }
    }

    @Override
    protected void renderImage(ActivityModel activity) {
        String shotImage = activity.getShot().getImage();
        if (shotImage != null) {
            image.setVisibility(View.VISIBLE);
            imageLoader.load(shotImage, image);
        } else {
            image.setVisibility(View.GONE);
        }
    }

    private CharSequence buildActivityCommentWithShot(ActivityModel activity) {
        return new Truss() //
          .append(getActivityCommentPrefix(activity)) //
          .pushSpan(new ForegroundColorSpan(shotCommentColor)) //
          .append(activity.getShot().getComment()) //
          .popSpan()//
          .build();
    }

    protected abstract String getActivitySimpleComment(ActivityModel activity);

    protected abstract String getActivityCommentPrefix(ActivityModel activity);
}
