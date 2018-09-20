package com.shootr.mobile.ui.adapters.holders;

import android.graphics.Typeface;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import butterknife.BindColor;
import butterknife.BindString;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnPollQuestionClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.ui.widgets.StreamTitleBoldSpan;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.PollQuestionSpannableBuilder;
import com.shootr.mobile.util.Truss;

public class PollSharedViewHolder extends GenericActivityViewHolder {

  private final PollQuestionSpannableBuilder pollQuestionSpannableBuilder;
  private final OnPollQuestionClickListener onPollQuestionClickListener;
  private final OnStreamTitleClickListener onStreamTitleClickListener;
  private final AndroidTimeUtils androidTimeUtils;
  @BindColor(R.color.gray_60) int gray_60;
  @BindString(R.string.poll_shared) String pollShared;

  public PollSharedViewHolder(View view, ImageLoader imageLoader, AndroidTimeUtils androidTimeUtils,
      OnAvatarClickListener onAvatarClickListener,
      PollQuestionSpannableBuilder pollQuestionSpannableBuilder,
      OnPollQuestionClickListener onPollQuestionClickListener,
      OnStreamTitleClickListener onStreamTitleClickListener) {
    super(view, imageLoader, androidTimeUtils, onAvatarClickListener);
    this.pollQuestionSpannableBuilder = pollQuestionSpannableBuilder;
    this.onPollQuestionClickListener = onPollQuestionClickListener;
    this.androidTimeUtils = androidTimeUtils;
    this.onStreamTitleClickListener = onStreamTitleClickListener;
  }

  @Override protected void renderText(ActivityModel activity) {
    try {
      title.setText(getFormattedUserName(activity));
      title.setVisibility(View.VISIBLE);
      title.setMovementMethod(new LinkMovementMethod());
      renderEmbedPollQuestion(activity);
    } catch (Exception e) {
      /* no-op */
    }
  }

  private void renderEmbedPollQuestion(final ActivityModel activity) {
    text.setVisibility(View.GONE);
    embedCard.setVisibility(View.VISIBLE);
    embedShotComment.setVisibility(View.GONE);
    embedShotImage.setVisibility(View.GONE);
    embedUsername.setText(getFormattedPoll(activity));
    embedCard.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        onPollQuestionClickListener.onPollQuestionClick(activity.getIdPoll(),
            activity.getStreamTitle());
      }
    });
  }

  private CharSequence getFormattedPoll(ActivityModel activity) {
    return new Truss()
        .pushSpan(new StyleSpan(Typeface.BOLD))
        .append(activity.getPollQuestion()).popSpan()
        .build();
  }

  @Override protected CharSequence getFormattedUserName(ActivityModel activity) {
    StreamTitleBoldSpan streamTitleSpan =
        new StreamTitleBoldSpan(activity.getIdStream(), activity.getStreamTitle()) {
          @Override
          public void onStreamClick(String streamId, String streamTitle) {
            onStreamTitleClickListener.onStreamTitleClick(streamId, streamTitle);
          }
        };
    return new Truss()
        .pushSpan(new StyleSpan(Typeface.BOLD))
        .append(activity.getUsername()).popSpan()
        .append(" ")
        .append(pollShared)
        .append(" ")
        .pushSpan(streamTitleSpan)
        .append(verifiedStream(activity.getStreamTitle(), activity.isVerified()))
        .popSpan()
        .pushSpan(new ForegroundColorSpan(gray_60))
        .append(" ")
        .append(androidTimeUtils.getElapsedTime(getContext(), activity.getPublishDate().getTime()))
        .popSpan()
        .build();
  }

  @Override protected void renderTargetAvatar(ActivityModel activity) {
    /* no-op */
  }

}
