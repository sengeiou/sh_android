package com.shootr.mobile.ui.adapters.streamtimeline.holder;

import android.view.View;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ahamed.multiviewadapter.BaseViewHolder;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.model.poll.PollStatus;
import com.shootr.mobile.ui.adapters.listeners.OnPollActionClickListener;
import com.shootr.mobile.ui.model.PollModel;

public class PollViewHolder extends BaseViewHolder<PollModel> {

  @BindView(R.id.poll_question) TextView pollQuestionTextView;
  @BindView(R.id.poll_action) TextView pollActionTextView;

  @BindString(R.string.poll_vote) String pollVoteString;
  @BindString(R.string.poll_view) String pollViewString;
  @BindString(R.string.timeline_poll_results) String pollResultsString;

  private final OnPollActionClickListener onPollActionClickListener;

  public PollViewHolder(View itemView, OnPollActionClickListener onPollActionClickListener) {
    super(itemView);
    this.onPollActionClickListener = onPollActionClickListener;
    ButterKnife.bind(this, itemView);
  }

  public void render(final PollModel pollModel) {
    pollQuestionTextView.setText(pollModel.getQuestion());
    setupPollAction(pollModel);
    itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onPollActionClickListener.onPollClick(pollModel);
      }
    });
  }

  private void setupPollAction(PollModel pollModel) {
    if (pollModel.getStatus().equals(PollStatus.CLOSED)) {
      pollActionTextView.setText(pollResultsString.toUpperCase());
      return;
    }
    handleVoteStatus(pollModel);
  }

  private void handleVoteStatus(PollModel pollModel) {
    if (pollModel.getVoteStatus().equals(PollStatus.VOTED) || pollModel.getVoteStatus()
        .equals(PollStatus.HASSEENRESULTS)) {
      pollActionTextView.setText(pollViewString.toUpperCase());
    } else {
      pollActionTextView.setText(pollVoteString.toUpperCase());
    }
  }
}
