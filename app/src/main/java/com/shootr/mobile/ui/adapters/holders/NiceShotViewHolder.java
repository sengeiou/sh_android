package com.shootr.mobile.ui.adapters.holders;

import android.graphics.Typeface;
import android.text.style.StyleSpan;
import android.view.View;
import butterknife.BindString;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotClick;
import com.shootr.mobile.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.widgets.StreamTitleSpan;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.Truss;

public class NiceShotViewHolder extends ShotActivityViewHolder {

  @BindString(R.string.niced_shot_activity) String nicedShotText;
  @BindString(R.string.niced_shot_activity_with_comment) String nicedShotPrefixText;

  public NiceShotViewHolder(View view, ImageLoader imageLoader, AndroidTimeUtils androidTimeUtils,
      OnAvatarClickListener onAvatarClickListener, OnShotClick onShotClickListener,
      OnStreamTitleClickListener onStreamTitleClickListener) {
    super(view, imageLoader, androidTimeUtils, onAvatarClickListener, onShotClickListener,
        onStreamTitleClickListener);
  }

  @Override protected CharSequence getTitle(ActivityModel activity) {
    StreamTitleSpan streamTitleSpan =
        new StreamTitleSpan(activity.getIdStream(), activity.getStreamTitle(),
            activity.getIdStreamAuthor()) {
          @Override
          public void onStreamClick(String streamId, String streamTitle, String idAuthor) {
            onStreamTitleClickListener.onStreamTitleClick(streamId, streamTitle, idAuthor);
          }
        };
    return new Truss().pushSpan(new StyleSpan(Typeface.BOLD))
        .append(activity.getUsername())
        .popSpan()
        .append(getActivitySimpleComment(activity))
        .append(" ")
        .pushSpan(new StyleSpan(Typeface.BOLD))
        .pushSpan(streamTitleSpan)
        .append(activity.getStreamTitle())
        .append(verifiedStream(activity.isVerified()))
        .popSpan()
        .build();
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
    return nicedShotPrefixText;
  }

}
