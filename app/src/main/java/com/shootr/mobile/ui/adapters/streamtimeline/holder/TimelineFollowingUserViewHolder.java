package com.shootr.mobile.ui.adapters.streamtimeline.holder;

import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ahamed.multiviewadapter.BaseViewHolder;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.PromotedItemClickListener;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.widgets.AvatarView;
import com.shootr.mobile.util.ImageLoader;

public class TimelineFollowingUserViewHolder extends BaseViewHolder<UserModel> {

  private final PromotedItemClickListener promotedItemClickListener;
  private final ImageLoader imageLoader;

  @BindView(R.id.user_avatar) AvatarView avatar;
  @BindView(R.id.seen) View seen;
  @BindView(R.id.name) TextView name;

  public TimelineFollowingUserViewHolder(View itemView,
      PromotedItemClickListener promotedItemClickListener, ImageLoader imageLoader) {
    super(itemView);
    this.promotedItemClickListener = promotedItemClickListener;
    this.imageLoader = imageLoader;
    ButterKnife.bind(this, itemView);
  }

  public void render(final UserModel user) {
    setupAvatar(user);
    name.setText(user.getUsername());
    setupSeen(user);
  }

  private void setupAvatar(final UserModel user) {
    imageLoader.loadProfilePhoto(user.getPhoto(), avatar, user.getUsername());
    avatar.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        promotedItemClickListener.onUserFollowingClick(user);
      }
    });
  }

  private void setupSeen(final UserModel user) {
    if (user.getSeen()) {
      seen.setVisibility(View.INVISIBLE);
    } else {
      seen.setVisibility(View.VISIBLE);
    }
  }

}
