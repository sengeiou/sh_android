package com.shootr.mobile.ui.adapters.holders;

import android.view.View;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnPollQuestionClickListener;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.PollVotedSpannableBuilder;

public class PollVotedViewHolder extends GenericActivityViewHolder {

  private final PollVotedSpannableBuilder pollVotedSpannableBuilder;
  private final OnPollQuestionClickListener onPollQuestionClickListener;

  public PollVotedViewHolder(View view, ImageLoader imageLoader, AndroidTimeUtils androidTimeUtils,
      OnAvatarClickListener onAvatarClickListener,
      PollVotedSpannableBuilder pollVotedSpannableBuilder,
      OnPollQuestionClickListener onPollQuestionClickListener) {
    super(view, imageLoader, androidTimeUtils, onAvatarClickListener);
    this.pollVotedSpannableBuilder = pollVotedSpannableBuilder;
    this.onPollQuestionClickListener = onPollQuestionClickListener;
  }

  @Override protected void renderText(ActivityModel activity) {
    text.setText(formatActivityComment(activity));
  }

  protected CharSequence formatActivityComment(final ActivityModel activity) {
    if (activity.getPollOptionText() != null && !activity.getPollOptionText().isEmpty()) {
      activity.setComment(itemView.getResources()
          .getString(R.string.voted_public_poll, activity.getPollOptionText(),
              activity.getStreamTitle()));
    } else {
      activity.setComment(
          itemView.getResources().getString(R.string.voted_poll, activity.getStreamTitle()));
    }
    return pollVotedSpannableBuilder.formatWithPollQuestionSpans(activity.getIdPoll(),
        activity.getStreamTitle(), activity.getPollQuestion(), activity.getComment(),
        onPollQuestionClickListener);
  }
}
