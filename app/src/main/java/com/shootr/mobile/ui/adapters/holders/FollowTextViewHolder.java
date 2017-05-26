package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnFollowUnfollowStreamListener;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.widgets.FollowButton;

public class FollowTextViewHolder extends RecyclerView.ViewHolder {

  private final OnFollowUnfollowStreamListener onFollowUnfollowStreamListener;
  private StreamModel streamModel;

  @BindView(R.id.text) TextView text;
  @BindView(R.id.stream_follow_button) FollowButton followButton;

  public FollowTextViewHolder(View itemView,
      OnFollowUnfollowStreamListener onFollowUnfollowStreamListener) {
    super(itemView);
    this.onFollowUnfollowStreamListener = onFollowUnfollowStreamListener;
    ButterKnife.bind(this, itemView);
  }

  public void setupFollowButtonListener() {
    followButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (followButton.isFollowing()) {
          onFollowUnfollowStreamListener.onUnfollow(streamModel);
        } else {
          onFollowUnfollowStreamListener.onFollow(streamModel);
        }
      }
    });
  }

  public void setText(String text) {
    this.text.setText(text);
    this.text.setVisibility(text != null ? View.VISIBLE : View.GONE);
    this.followButton.setVisibility(View.VISIBLE);
  }

  public void setStream(StreamModel streamModel) {
    this.streamModel = streamModel;
  }

  public void setFollowing(Boolean isFollowing) {
    this.followButton.setFollowing(isFollowing);
  }
}
