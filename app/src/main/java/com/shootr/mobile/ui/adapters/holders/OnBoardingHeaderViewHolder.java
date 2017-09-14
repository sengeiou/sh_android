package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.OnBoardingStreamsAdapter;

public class OnBoardingHeaderViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.onboarding_header_text) TextView text;
  @BindString(R.string.onboarding_streams_header) String streamsHeader;

  private final String type;

  public OnBoardingHeaderViewHolder(View itemView, String type) {
    super(itemView);
    this.type = type;
    ButterKnife.bind(this, itemView);
  }

  public void render() {
    if (type.equals(OnBoardingStreamsAdapter.STREAM_ONBOARDING)) {
      text.setText(streamsHeader);
    } else {
      //TODO
    }
  }
}
