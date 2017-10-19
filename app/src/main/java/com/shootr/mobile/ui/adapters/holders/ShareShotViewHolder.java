package com.shootr.mobile.ui.adapters.holders;

import android.graphics.Typeface;
import android.text.style.StyleSpan;
import android.view.View;
import butterknife.BindString;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotClick;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.Truss;

public class ShareShotViewHolder extends ShotActivityViewHolder {

    @BindString(R.string.shared_shot_activity) String sharedShotPattern;
    @BindString(R.string.shared_shot_activity_with_comment) String sharedShotPrefixPattern;

    public ShareShotViewHolder(View view, ImageLoader imageLoader, AndroidTimeUtils androidTimeUtils,
      OnAvatarClickListener onAvatarClickListener, OnShotClick onShotClickListener) {
        super(view, imageLoader, androidTimeUtils, onAvatarClickListener, onShotClickListener);
    }

    @Override protected CharSequence getTitle(ActivityModel activity) {
        return new Truss()
            .pushSpan(new StyleSpan(Typeface.BOLD))
            .append(activity.getUsername()).popSpan()
            .append(getActivitySimpleComment(activity)).append(" ")
            .pushSpan(new StyleSpan(Typeface.BOLD))
            .append(activity.getStreamTitle()).popSpan().build();
    }

    @Override protected void renderText(ActivityModel activity) {
        text.setVisibility(View.GONE);
        image.setVisibility(View.GONE);
        embedCard.setVisibility(View.VISIBLE);

        ShotModel shot = activity.getShot();

        embedUsername.setText(activity.getShot().getUsername());
        renderEmbedComment(shot);
    }

    @Override protected void renderImage(ActivityModel activity) {
        if (activity.getShot().getImage() != null) {
            renderEmbedImage(activity.getShot());
        }
    }

    private void renderEmbedImage(ShotModel shot) {
        if (shot.getImage().getImageUrl() != null) {
            imageLoader.load(shot.getImage().getImageUrl(), embedShotImage);
            embedShotImage.setVisibility(View.VISIBLE);
        } else {
            embedShotImage.setVisibility(View.GONE);
        }
    }

    private void renderEmbedComment(ShotModel shotModel) {
        if (shotModel.getComment() != null) {
            embedShotComment.setBaseMessageModel(shotModel);
            embedShotComment.setText(shotModel.getComment());
            embedShotComment.addLinks();
        } else {
            embedShotComment.setVisibility(View.GONE);
        }
    }

    @Override protected String getActivitySimpleComment(ActivityModel activity) {
        return sharedShotPrefixPattern;
    }

    @Override
    protected String getActivityCommentPrefix(ActivityModel activity) {
        return sharedShotPrefixPattern;
    }
}
