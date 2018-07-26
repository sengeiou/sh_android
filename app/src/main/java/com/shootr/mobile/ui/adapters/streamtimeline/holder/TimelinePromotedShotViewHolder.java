package com.shootr.mobile.ui.adapters.streamtimeline.holder;

import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ahamed.multiviewadapter.BaseViewHolder;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.model.PrintableItem;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.PromotedItemClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.widgets.AvatarView;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.PromotedColorManager;

public class TimelinePromotedShotViewHolder extends BaseViewHolder<ShotModel> {

  private final PromotedItemClickListener promotedItemClickListener;
  private final ImageLoader imageLoader;
  private final OnShotLongClick onShotLongClick;

  @BindView(R.id.shot_avatar) AvatarView avatar;
  @BindView(R.id.seen) View seen;
  @BindView(R.id.name) TextView name;
  @BindView(R.id.price) TextView price;

  private PromotedColorManager promotedColorManager;

  public TimelinePromotedShotViewHolder(View itemView,
      PromotedItemClickListener promotedItemClickListener, ImageLoader imageLoader,
      OnShotLongClick onShotLongClick) {
    super(itemView);
    this.promotedItemClickListener = promotedItemClickListener;
    this.imageLoader = imageLoader;
    promotedColorManager = new PromotedColorManager(itemView.getContext());
    this.onShotLongClick = onShotLongClick;
    ButterKnife.bind(this, itemView);
  }

  public void render(final ShotModel shot) {
    imageLoader.loadProfilePhoto(shot.getAvatar(), avatar, shot.getUsername());
    avatar.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        promotedItemClickListener.markSeen(PrintableItem.SHOT, shot.getIdShot());
        promotedItemClickListener.onPromotedShotClick(shot);
      }
    });
    name.setText(shot.getUsername());
    setupSeen(shot);
    setupBubbleBackground(shot);
    setupPrice(shot);
    avatar.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View v) {
        onShotLongClick.onShotLongClick(shot);
        return false;
      }
    });
  }

  public void setupSeen(final ShotModel shot) {
    if (shot.getSeen()) {
      seen.setVisibility(View.INVISIBLE);
    } else {
      seen.setVisibility(View.VISIBLE);
    }
  }

  private void setupBubbleBackground(ShotModel shot) {
    price.setBackground(promotedColorManager.getBackgroundForPromotedMark(shot));
  }

  private void setupPrice(ShotModel shotModel) {
    String promotedPrice = promotedColorManager.getPromotedPrice(shotModel);
    price.setText(promotedPrice);
  }
}
