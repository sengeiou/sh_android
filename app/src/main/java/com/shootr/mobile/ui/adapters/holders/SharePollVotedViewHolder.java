package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.util.ImageLoader;
import de.hdodenhof.circleimageview.CircleImageView;

public class SharePollVotedViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.poll_option) TextView pollOption;
  @BindView(R.id.share_option) TextView shareOption;
  @BindView(R.id.poll_question) TextView pollQuestion;
  @BindView(R.id.poll_option_image) CircleImageView optionImage;

  private final View.OnClickListener onClickListener;
  private final ImageLoader imageLoader;

  public SharePollVotedViewHolder(View itemView, View.OnClickListener onClickListener,
      ImageLoader imageLoader) {
    super(itemView);
    this.onClickListener = onClickListener;
    this.imageLoader = imageLoader;
    ButterKnife.bind(this, itemView);
  }

  public void render(final PollModel model) {

    pollOption.setText(getPollOptionText(model));
    shareOption.setOnClickListener(onClickListener);
    pollQuestion.setText(model.getQuestion());
  }

  private String getPollOptionText(PollModel model) {
    String shareResource = itemView.getContext().getString(R.string.share_poll_voted_comment);
    String pollVotedText = "";

    for (PollOptionModel pollOptionModel : model.getPollOptionModels()) {
      if (pollOptionModel.isVoted()) {
        pollVotedText = pollOptionModel.getText();
        setupImage(pollOptionModel);
        break;
      }
    }

    return String.format(shareResource, pollVotedText);
  }

  private void setupImage(PollOptionModel pollOptionModel) {
    if (pollOptionModel.getOptionImage() != null) {
      optionImage.setVisibility(View.VISIBLE);
      imageLoader.loadStreamPicture(
          pollOptionModel.getOptionImage().getSizes().getLow().getUrl(), optionImage);
    } else {
      optionImage.setVisibility(View.GONE);
    }
  }
}
