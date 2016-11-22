package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnPollStreamTitleClickListener;
import com.shootr.mobile.ui.model.PollModel;

public class PollQuestionViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.poll_question) TextView pollQuestion;
  @BindView(R.id.stream_title) TextView streamTitle;

  private final OnPollStreamTitleClickListener onPollStreamTitleClickListener;

  public PollQuestionViewHolder(View itemView,
      OnPollStreamTitleClickListener onPollStreamTitleClickListener) {
    super(itemView);
    this.onPollStreamTitleClickListener = onPollStreamTitleClickListener;
    ButterKnife.bind(this, itemView);
  }

  public void render(final PollModel model) {
    pollQuestion.setText(model.getQuestion());
    streamTitle.setText(model.getStreamTitle());
    streamTitle.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onPollStreamTitleClickListener.onClickPressed();
      }
    });
  }

}
