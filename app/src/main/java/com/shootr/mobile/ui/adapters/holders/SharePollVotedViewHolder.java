package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PollOptionModel;

public class SharePollVotedViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.poll_option) TextView pollOption;
  @BindView(R.id.share_option) TextView shareOption;

  private final View.OnClickListener onClickListener;

  public SharePollVotedViewHolder(View itemView,
      View.OnClickListener onClickListener) {
    super(itemView);
    this.onClickListener = onClickListener;
    ButterKnife.bind(this, itemView);
  }

  public void render(final PollModel model) {

    pollOption.setText(getPollOptionText(model));
    shareOption.setOnClickListener(onClickListener);
  }

  private String getPollOptionText(PollModel model) {
    String shareResource = itemView.getContext().getString(R.string.share_poll_voted_comment);
    String pollVotedText = "";

    for (PollOptionModel pollOptionModel : model.getPollOptionModels()) {
      if (pollOptionModel.getIdPollOption().equals(model.getIdPollOptionVoted())) {
        pollVotedText = pollOptionModel.getText();
      }
    }

    return String.format(shareResource, pollVotedText);
  }
}
