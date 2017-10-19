package com.shootr.mobile.ui.adapters.holders;

import android.graphics.Typeface;
import android.text.style.StyleSpan;
import android.view.View;
import butterknife.BindColor;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnPollQuestionClickListener;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.PollQuestionSpannableBuilder;
import com.shootr.mobile.util.Truss;

public class PollFinishedViewHolder extends GenericActivityViewHolder {

  private final PollQuestionSpannableBuilder pollQuestionSpannableBuilder;
  private final OnPollQuestionClickListener onPollQuestionClickListener;
  private final AndroidTimeUtils androidTimeUtils;
  @BindColor(R.color.gray_60) int gray_60;

  public PollFinishedViewHolder(View view, ImageLoader imageLoader,
      AndroidTimeUtils androidTimeUtils, OnAvatarClickListener onAvatarClickListener,
      PollQuestionSpannableBuilder pollQuestionSpannableBuilder,
      OnPollQuestionClickListener onPollQuestionClickListener) {
    super(view, imageLoader, androidTimeUtils, onAvatarClickListener);
    this.pollQuestionSpannableBuilder = pollQuestionSpannableBuilder;
    this.onPollQuestionClickListener = onPollQuestionClickListener;
    this.androidTimeUtils = androidTimeUtils;
  }

  @Override protected void renderText(ActivityModel activity) {
    try {
      title.setText(getFormattedUserName(activity));
      title.setVisibility(View.VISIBLE);
      renderEmbedPollQuestion(activity);
    } catch (Exception e) {
      /* no-op */
    }
  }

  private void renderEmbedPollQuestion(ActivityModel activity) {
    text.setVisibility(View.GONE);
    embedCard.setVisibility(View.VISIBLE);
    embedShotComment.setVisibility(View.GONE);
    embedShotImage.setVisibility(View.GONE);
    embedUsername.setText(getFormattedPoll(activity));
  }

  private CharSequence getFormattedPoll(ActivityModel activity) {
    return new Truss()
        .pushSpan(new StyleSpan(Typeface.BOLD))
        .append(activity.getPollQuestion()).popSpan()
        .build();
  }

  @Override protected CharSequence getFormattedUserName(ActivityModel activity) {
    return new Truss()
        .append("Poll has finished in")
        .append(" ")
        .pushSpan(new StyleSpan(Typeface.BOLD))
        .append(activity.getStreamTitle()).popSpan()
        .build();
  }

  @Override protected void renderAvatar(ActivityModel activity) {
    //TODO poner foto de stream
    imageLoader.loadProfilePhoto(activity.getUserPhoto(), avatar, activity.getStreamTitle());
    avatar.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //TODO onAvatarClickListener.onAvatarClick(activity.getIdUser(), avatar);
      }
    });
  }

  @Override protected void rendetTargetAvatar(ActivityModel activity) {
    /* no-op */
  }

  protected CharSequence formatActivityComment(final ActivityModel activity) {
    activity.setComment(
        itemView.getResources().getString(R.string.finished_poll, activity.getPollQuestion()));
    return pollQuestionSpannableBuilder.formatPollQuestionSpans(activity.getIdPoll(),
        activity.getStreamTitle(), activity.getPollQuestion(), activity.getComment(),
        onPollQuestionClickListener);
  }
}
