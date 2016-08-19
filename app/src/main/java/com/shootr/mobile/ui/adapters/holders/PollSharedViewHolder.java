package com.shootr.mobile.ui.adapters.holders;

import android.view.View;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnPollQuestionClickListener;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.PollQuestionSpannableBuilder;

public class PollSharedViewHolder extends GenericActivityViewHolder {

  private final PollQuestionSpannableBuilder pollQuestionSpannableBuilder;
  private final OnPollQuestionClickListener onPollQuestionClickListener;

  public PollSharedViewHolder(View view, ImageLoader imageLoader,
      AndroidTimeUtils androidTimeUtils, OnAvatarClickListener onAvatarClickListener,
      PollQuestionSpannableBuilder pollQuestionSpannableBuilder,
      OnPollQuestionClickListener onPollQuestionClickListener) {
    super(view, imageLoader, androidTimeUtils, onAvatarClickListener);
    this.pollQuestionSpannableBuilder = pollQuestionSpannableBuilder;
    this.onPollQuestionClickListener = onPollQuestionClickListener;
  }

  @Override protected void renderText(ActivityModel activity) {
    text.setText(formatActivityComment(activity));
  }

  protected CharSequence formatActivityComment(final ActivityModel activity) {
    activity.setComment(itemView.getResources()
        .getString(R.string.shared_poll, activity.getPollQuestion()));
    return pollQuestionSpannableBuilder.formatPollQuestionSpans(activity.getIdPoll(),
        activity.getPollQuestion(),
        activity.getComment(), onPollQuestionClickListener);
  }
}
