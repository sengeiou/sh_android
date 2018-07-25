package com.shootr.mobile.ui.adapters;

import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.RecyclerAdapter;
import com.ahamed.multiviewadapter.util.PayloadProvider;
import com.shootr.mobile.ui.adapters.binder.PromotedLandingStreamBinder;
import com.shootr.mobile.ui.adapters.listeners.PromotedItemsClickListener;
import com.shootr.mobile.ui.model.PromotedGroupModel;
import com.shootr.mobile.ui.model.PromotedLandingItemModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;

public class PromotedLandingAdapter extends RecyclerAdapter {

  private final PromotedItemsClickListener promotedItemsClickListener;
  private final ImageLoader imageLoader;
  private final NumberFormatUtil numberFormatUtil;

  private DataListManager<PromotedLandingItemModel> promotedItems;

  public PromotedLandingAdapter(PromotedItemsClickListener promotedItemsClickListener,
      ImageLoader imageLoader, NumberFormatUtil numberFormatUtil) {
    this.promotedItemsClickListener = promotedItemsClickListener;
    this.imageLoader = imageLoader;
    this.numberFormatUtil = numberFormatUtil;
    setupList();
  }

  private void setupList() {
    promotedItems = new DataListManager<>(this, new PayloadProvider<PromotedLandingItemModel>() {
      @Override public boolean areContentsTheSame(PromotedLandingItemModel oldItem, PromotedLandingItemModel newItem) {
        return oldItem.equals(newItem);
      }

      @Override public Object getChangePayload(PromotedLandingItemModel oldItem, PromotedLandingItemModel newItem) {
        return null;
      }
    });
    addDataManager(promotedItems);

    registerBinder(new PromotedLandingStreamBinder(promotedItemsClickListener, imageLoader, numberFormatUtil));
  }

  public void setList(PromotedGroupModel promotedGroupModel) {
    promotedItems.set(promotedGroupModel.getPromotedModels());
  }

  public PromotedLandingItemModel getItem(int position) {

    return promotedItems.get(position);
  }

}
