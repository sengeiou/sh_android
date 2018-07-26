package com.shootr.mobile.ui.adapters.streamtimeline.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ahamed.multiviewadapter.BaseViewHolder;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.PromotedItemClickListener;
import com.shootr.mobile.util.ImageLoader;

public class AddPromotedShotViewHolder extends BaseViewHolder<Boolean> {

  private static final int SUPER_SHOT_IMAGE = R.drawable.ic_new_super_shot;

  private final PromotedItemClickListener promotedItemClickListener;
  private final ImageLoader imageLoader;

  @BindView(R.id.avatar) ImageView avatar;
  @BindView(R.id.name) TextView name;
  @BindString(R.string.super_shot) String superShotResource;

  public AddPromotedShotViewHolder(View itemView,
      PromotedItemClickListener promotedItemClickListener, ImageLoader imageLoader) {
    super(itemView);
    this.promotedItemClickListener = promotedItemClickListener;
    this.imageLoader = imageLoader;
    ButterKnife.bind(this, itemView);
  }

  public void render() {
    avatar.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        promotedItemClickListener.onAddPromotedPressed();
      }
    });
    name.setText(superShotResource);
  }
}
