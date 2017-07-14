package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.util.NumberFormatUtil;

public class SeparatorViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.title) TextView title;
  @BindView(R.id.following) TextView following;
  @BindString(R.string.participants) String participantsSource;

  private final NumberFormatUtil numberFormatUtil;

  public SeparatorViewHolder(View itemView, NumberFormatUtil numberFormatUtil) {
    super(itemView);
    this.numberFormatUtil = numberFormatUtil;
    ButterKnife.bind(this, itemView);
  }

  public void showTitle() {
    title.setVisibility(View.VISIBLE);
  }

  public void setFollowingNumber(Integer numberOfFollowing, Integer totalWatchers) {

    if (numberOfFollowing == 0) {
      following.setVisibility(View.GONE);
    } else {
      following.setVisibility(View.VISIBLE);
      following.setText(following.getContext()
          .getResources()
          .getString(R.string.stream_detail_following,
              numberFormatUtil.formatNumbers(numberOfFollowing.longValue())));
    }

    if (totalWatchers == 0) {
      title.setVisibility(View.VISIBLE);
      title.setText(participantsSource);
    } else {
      title.setVisibility(View.VISIBLE);
      title.setText(title.getContext()
          .getResources()
          .getQuantityString(R.plurals.stream_detail_participants, totalWatchers,
              numberFormatUtil.formatNumbers(totalWatchers.longValue())));
    }
  }
}
