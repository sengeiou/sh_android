package com.shootr.mobile.ui.adapters.streamtimeline.holder;

import android.view.View;
import android.widget.ImageView;
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

public class TimelineHighlightedShotViewHolder extends BaseViewHolder<ShotModel> {

  private final PromotedItemClickListener promotedItemClickListener;
  private final OnShotLongClick onShotLongClick;
  private final ImageLoader imageLoader;

  @BindView(R.id.shot_avatar) AvatarView avatar;
  @BindView(R.id.highlighted_mark) ImageView mark;
  @BindView(R.id.seen) View seen;
  @BindView(R.id.name) TextView name;

  public TimelineHighlightedShotViewHolder(View itemView,
      PromotedItemClickListener promotedItemClickListener, OnShotLongClick onShotLongClick,
      ImageLoader imageLoader) {
    super(itemView);
    this.promotedItemClickListener = promotedItemClickListener;
    this.onShotLongClick = onShotLongClick;
    this.imageLoader = imageLoader;
    ButterKnife.bind(this, itemView);
  }

  public void render(final ShotModel shot) {
    imageLoader.loadProfilePhoto(shot.getAvatar(), avatar, shot.getUsername());
    avatar.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        promotedItemClickListener.markSeen(PrintableItem.SHOT, shot.getIdShot());
        promotedItemClickListener.onHighlightedClick(shot);
      }
    });
    name.setText(shot.getUsername());
    setupSeenMark(shot);
    avatar.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View v) {
        onShotLongClick.onShotLongClick(shot);
        return false;
      }
    });
  }

  public void setupSeenMark(ShotModel shotModel) {
    if (shotModel.getSeen()) {
      seen.setVisibility(View.INVISIBLE);
    } else {
      seen.setVisibility(View.VISIBLE);
    }
  }
}
