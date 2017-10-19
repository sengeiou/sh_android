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
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.Truss;

public abstract class ShotActivityViewHolder extends GenericActivityViewHolder {

  protected final ImageLoader imageLoader;
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
    title.setText(getTitle(activity));
  }

  protected CharSequence getTitle(ActivityModel activity) {
    return new Truss()
        .pushSpan(new StyleSpan(Typeface.BOLD))
        .append(activity.getUsername()).popSpan()
        .append(getActivitySimpleComment(activity)).append(" ")
        .pushSpan(new StyleSpan(Typeface.BOLD))
        .append(activity.getUsername()).popSpan().build();
  }

  private void enableShotClick(final ActivityModel activity) {
    itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        onShotClickListener.onShotClick(activity.getShot());
      }
    });
  }

  @Override protected void rendetTargetAvatar(ActivityModel activity) {
    targetAvatar.setVisibility(View.GONE);
  }

  @Override protected void renderText(ActivityModel activity) {
    ShotModel shotModel = activity.getShot();
    if (shotModel.getComment() != null) {
      text.setBaseMessageModel(shotModel);
      text.setText(shotModel.getComment());
      text.addLinks();
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

  protected abstract String getActivitySimpleComment(ActivityModel activity);

  protected abstract String getActivityCommentPrefix(ActivityModel activity);
}
