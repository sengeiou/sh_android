package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;

public class LegalTextInPollViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.poll_legal_text) TextView pollLegalText;


  public LegalTextInPollViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
  }

  public void render(String legalText) {
    pollLegalText.setText(legalText);
  }

}
