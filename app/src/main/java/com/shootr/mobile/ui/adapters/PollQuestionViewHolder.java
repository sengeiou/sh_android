package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;

public class PollQuestionViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.poll_question) TextView pollQuestion;

  public PollQuestionViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
  }

  public void render() {
   /* no-op */
  }

}
