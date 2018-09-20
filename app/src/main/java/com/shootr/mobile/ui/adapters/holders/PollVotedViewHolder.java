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
import com.shootr.mobile.util.PollVotedSpannableBuilder;
import com.shootr.mobile.util.Truss;

public class PollVotedViewHolder extends GenericActivityViewHolder {

  private final PollVotedSpannableBuilder pollVotedSpannableBuilder;
  private final OnPollQuestionClickListener onPollQuestionClickListener;
  private final OnStreamTitleClickListener onStreamTitleClickListener;
  private final AndroidTimeUtils androidTimeUtils;
  @BindColor(R.color.gray_60) int gray_60;
  @BindString(R.string.voted_in_a_poll) String votedResource;

  public PollVotedViewHolder(View view, ImageLoader imageLoader, AndroidTimeUtils androidTimeUtils,
      OnAvatarClickListener onAvatarClickListener,
      PollVotedSpannableBuilder pollVotedSpannableBuilder,
      OnPollQuestionClickListener onPollQuestionClickListener,
      OnStreamTitleClickListener onStreamTitleClickListener) {
    super(view, imageLoader, androidTimeUtils, onAvatarClickListener);
    this.pollVotedSpannableBuilder = pollVotedSpannableBuilder;
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
    embedShotImage.setVisibility(View.GONE);
    embedUsername.setText(getFormattedPoll(activity));
    embedCard.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        onPollQuestionClickListener.onPollQuestionClick(activity.getIdPoll(),
            activity.getStreamTitle());
      }
    });

    if (activity.getPollOptionText() != null) {
      embedShotComment.setText(activity.getPollOptionText());
      embedShotComment.setVisibility(View.VISIBLE);
    } else {
      embedUsername.setPadding(getDps(8), getDps(8), getDps(8), getDps(8));
      embedShotComment.setVisibility(View.GONE);
    }
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
        .append(votedResource)
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
