package com.shootr.mobile.ui.adapters.binder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ahamed.multiviewadapter.ItemBinder;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.holders.PromotedLandingStreamViewHolder;
import com.shootr.mobile.ui.adapters.listeners.PromotedItemsClickListener;
import com.shootr.mobile.ui.model.PromotedLandingItemModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;

public class PromotedLandingStreamBinder
    extends ItemBinder<PromotedLandingItemModel, PromotedLandingStreamViewHolder> {

  private final PromotedItemsClickListener promotedItemsClickListener;
  private final ImageLoader imageLoader;
  private final NumberFormatUtil numberFormatUtil;

  public PromotedLandingStreamBinder(PromotedItemsClickListener promotedItemsClickListener,
      ImageLoader imageLoader, NumberFormatUtil numberFormatUtil) {
    this.promotedItemsClickListener = promotedItemsClickListener;
    this.imageLoader = imageLoader;
    this.numberFormatUtil = numberFormatUtil;
  }

  @Override
  public PromotedLandingStreamViewHolder create(LayoutInflater inflater, ViewGroup parent) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_promoted_stream_card, parent, false);
    return new PromotedLandingStreamViewHolder(view, promotedItemsClickListener, imageLoader,
        numberFormatUtil);
  }

  @Override
  public void bind(PromotedLandingStreamViewHolder holder, PromotedLandingItemModel item) {
    holder.render(item);
  }

  @Override public boolean canBindData(Object item) {
    return item instanceof PromotedLandingItemModel
        && ((PromotedLandingItemModel) item).getData() instanceof StreamModel;
  }
}
