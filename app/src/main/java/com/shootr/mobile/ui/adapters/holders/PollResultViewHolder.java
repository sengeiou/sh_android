package com.shootr.mobile.ui.adapters.holders;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.amulyakhare.textdrawable.TextDrawable;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnPollOptionClickListener;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.PercentageUtils;
import de.hdodenhof.circleimageview.CircleImageView;

public class PollResultViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.progressBar) ProgressBar progressBar;
  @BindView(R.id.option_picture) CircleImageView picture;
  @BindView(R.id.option_picture_with_text) ImageView pictureWithText;
  @BindView(R.id.percentage) TextView percentage;
  @BindView(R.id.poll_question) TextView question;
  @BindView(R.id.votes) TextView votes;

  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;
  private final PercentageUtils percentageUtils;
  private final Long totalVotes;
  private final Context context;
  private final OnPollOptionClickListener onPollOptionClickListener;

  public PollResultViewHolder(View itemView, OnPollOptionClickListener onPollOptionClickListener,
      ImageLoader imageLoader, InitialsLoader initialsLoader, PercentageUtils percentageUtils, Long totalVotes) {
    super(itemView);
    ButterKnife.bind(this, itemView);
    this.context = itemView.getContext();
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    this.percentageUtils = percentageUtils;
    this.totalVotes = totalVotes;
    this.onPollOptionClickListener = onPollOptionClickListener;
  }

  public void render(PollOptionModel model, Integer position) {
    setupImage(model);
    question.setText(
        context.getResources().getString(R.string.order_poll_option, position, model.getText()));
    setupVotes(model);
  }

  private void setupVotes(PollOptionModel model) {
    Double pollVotes = percentageUtils.getPercentage(model.getVotes(), totalVotes);
    String percentageString =
        context.getString(R.string.poll_results_pct, percentageUtils.formatPercentage(pollVotes));
    percentage.setText(percentageString);
    progressBar.setProgress(pollVotes.intValue());
    progressBar.getProgressDrawable()
        .setColorFilter(initialsLoader.getColorForLetters(model.getText()), PorterDuff.Mode.SRC_IN);
    votes.setText(String.valueOf(model.getVotes()));
  }

  private void setupImage(final PollOptionModel model) {
    if (model.getImageUrl() != null) {
      pictureWithText.setVisibility(View.GONE);
      picture.setVisibility(View.VISIBLE);
      imageLoader.loadStreamPicture(model.getImageUrl(), picture);
      picture.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          onPollOptionClickListener.onClickPressed(model);
        }
      });
    } else {
      pictureWithText.setVisibility(View.VISIBLE);
      picture.setVisibility(View.GONE);
      setupInitials(model, pictureWithText);
    }
  }

  private void setupInitials(PollOptionModel model, ImageView picture) {
    String initials = initialsLoader.getLetters(model.getText());
    int backgroundColor = initialsLoader.getColorForLetters(initials);
    TextDrawable textDrawable = initialsLoader.getCustomTextDrawable(initials, backgroundColor, 56, 56, 24);
    picture.setImageDrawable(textDrawable);
  }
}
