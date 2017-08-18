package com.shootr.mobile.ui.adapters.holders;

import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import butterknife.BindColor;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotClick;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.Truss;

public abstract class ShotActivityViewHolder extends GenericActivityViewHolder {

  private final ImageLoader imageLoader;
  private final OnShotClick onShotClickListener;
  private final AndroidTimeUtils androidTimeUtils;

  @BindColor(R.color.material_black) int shotCommentColor;
  @BindColor(R.color.gray_60) int gray_60;

  public ShotActivityViewHolder(View view, ImageLoader imageLoader,
      AndroidTimeUtils androidTimeUtils, OnAvatarClickListener onAvatarClickListener,
      OnShotClick onShotClickListener) {
    super(view, imageLoader, androidTimeUtils, onAvatarClickListener);
    this.imageLoader = imageLoader;
    this.onShotClickListener = onShotClickListener;
    this.androidTimeUtils = androidTimeUtils;
  }

  @Override public void render(ActivityModel activity) {
    super.render(activity);
    enableShotClick(activity);
  }

  private void enableShotClick(final ActivityModel activity) {
    itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        onShotClickListener.onShotClick(activity.getShot());
      }
    });
  }

  @Override protected void renderText(ActivityModel activity) {
    CharSequence activityText = getActivityText(activity);
    text.setText(activityText);
  }

  private CharSequence getActivityText(ActivityModel activity) {
    boolean hasCommentInShot = activity.getShot().getComment() != null;
    if (hasCommentInShot) {
      return buildActivityCommentWithShot(activity);
    } else {
      return buildActivitySimpleComment(activity);
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

  private CharSequence buildActivityCommentWithShot(ActivityModel activity) {
    return new Truss()
        .pushSpan(new StyleSpan(Typeface.BOLD))
        .append(activity.getUsername()).popSpan()
        .append(getActivityCommentPrefix(activity))
        .pushSpan(new ForegroundColorSpan(shotCommentColor))
        .append(activity.getShot().getComment())
        .popSpan()
        .pushSpan(new ForegroundColorSpan(gray_60))
        .append(" ")
        .append(androidTimeUtils.getElapsedTime(getContext(), activity.getPublishDate().getTime()))
        .popSpan()
        .build();
  }

  private CharSequence buildActivitySimpleComment(ActivityModel activity) {
    return new Truss()
        .pushSpan(new StyleSpan(Typeface.BOLD))
        .append(activity.getUsername()).popSpan()
        .pushSpan(new ForegroundColorSpan(shotCommentColor))
        .append(getActivitySimpleComment(activity))
        .popSpan()
        .pushSpan(new ForegroundColorSpan(gray_60))
        .append(" ")
        .append(androidTimeUtils.getElapsedTime(getContext(), activity.getPublishDate().getTime()))
        .popSpan()
        .build();
  }

  protected abstract String getActivitySimpleComment(ActivityModel activity);

  protected abstract String getActivityCommentPrefix(ActivityModel activity);
}
