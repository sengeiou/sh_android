package com.shootr.mobile.ui.adapters.holders;

import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import butterknife.BindColor;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnPollQuestionClickListener;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.PollVotedSpannableBuilder;
import com.shootr.mobile.util.Truss;

public class PollVotedViewHolder extends GenericActivityViewHolder {

  private final PollVotedSpannableBuilder pollVotedSpannableBuilder;
  private final OnPollQuestionClickListener onPollQuestionClickListener;
  private final AndroidTimeUtils androidTimeUtils;
  @BindColor(R.color.gray_60) int gray_60;

  public PollVotedViewHolder(View view, ImageLoader imageLoader, AndroidTimeUtils androidTimeUtils,
      OnAvatarClickListener onAvatarClickListener,
      PollVotedSpannableBuilder pollVotedSpannableBuilder,
      OnPollQuestionClickListener onPollQuestionClickListener) {
    super(view, imageLoader, androidTimeUtils, onAvatarClickListener);
    this.pollVotedSpannableBuilder = pollVotedSpannableBuilder;
    this.onPollQuestionClickListener = onPollQuestionClickListener;
    this.androidTimeUtils = androidTimeUtils;
  }

  @Override protected void renderText(ActivityModel activity) {
    text.setText(getFormattedUserName(activity));
  }

  @Override protected CharSequence getFormattedUserName(ActivityModel activity) {
    return new Truss()
        .pushSpan(new StyleSpan(Typeface.BOLD))
        .append(activity.getUsername()).popSpan()
        .append(formatActivityComment(activity))
        .pushSpan(new ForegroundColorSpan(gray_60))
        .append(" ")
        .append(androidTimeUtils.getElapsedTime(getContext(), activity.getPublishDate().getTime()))
        .popSpan()
        .build();
  }

  protected CharSequence formatActivityComment(final ActivityModel activity) {
    if (activity.getPollOptionText() != null && !activity.getPollOptionText().isEmpty()) {
      activity.setComment(itemView.getResources()
          .getString(R.string.voted_public_poll, activity.getPollOptionText()));
    } else {
      activity.setComment(itemView.getResources().getString(R.string.voted_poll));
    }
    return pollVotedSpannableBuilder.formatWithPollQuestionSpans(activity.getIdPoll(),
        activity.getStreamTitle(), activity.getPollQuestion(), activity.getComment(),
        onPollQuestionClickListener);
  }
}
