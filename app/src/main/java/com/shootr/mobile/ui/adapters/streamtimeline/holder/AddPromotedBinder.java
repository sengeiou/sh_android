package com.shootr.mobile.ui.adapters.streamtimeline.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ahamed.multiviewadapter.ItemBinder;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.PromotedItemClickListener;
import com.shootr.mobile.util.ImageLoader;

public class AddPromotedBinder extends ItemBinder<Boolean, AddPromotedShotViewHolder> {

  private final PromotedItemClickListener promotedItemClickListener;
  private final ImageLoader imageLoader;

  public AddPromotedBinder(PromotedItemClickListener promotedItemClickListener, ImageLoader imageLoader) {
    this.promotedItemClickListener = promotedItemClickListener;
    this.imageLoader = imageLoader;
  }

  @Override
  public AddPromotedShotViewHolder create(LayoutInflater inflater, ViewGroup parent) {
    View view = inflater.inflate(R.layout.item_timeline_add_promoted, parent, false);
    return new AddPromotedShotViewHolder(view, promotedItemClickListener, imageLoader);
  }

  @Override public void bind(AddPromotedShotViewHolder holder, Boolean item) {
    holder.render();
  }

  @Override public boolean canBindData(Object item) {
    return item instanceof Boolean;
  }
}
