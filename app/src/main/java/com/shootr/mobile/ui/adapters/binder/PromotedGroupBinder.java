package com.shootr.mobile.ui.adapters.binder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ahamed.multiviewadapter.ItemBinder;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.holders.PromotedGroupViewHolder;
import com.shootr.mobile.ui.adapters.listeners.PromotedItemsClickListener;
import com.shootr.mobile.ui.model.PromotedGroupModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;

public class PromotedGroupBinder extends ItemBinder<PromotedGroupModel, PromotedGroupViewHolder> {

  private final PromotedItemsClickListener promotedItemsClickListener;
  private final ImageLoader imageLoader;
  private final NumberFormatUtil numberFormatUtil;
  private final ChangesListener changesListener;

  private int positionInList = 0;
  private int offsetInList = 0;

  public PromotedGroupBinder(PromotedItemsClickListener promotedItemsClickListener,
      ImageLoader imageLoader, NumberFormatUtil numberFormatUtil) {
    this.promotedItemsClickListener = promotedItemsClickListener;
    this.imageLoader = imageLoader;
    this.numberFormatUtil = numberFormatUtil;
    changesListener = new ChangesListener() {
      @Override public void savePosition(int position, int offset) {
        positionInList = position;
        offsetInList = offset;
      }
    };
  }

  @Override public PromotedGroupViewHolder create(LayoutInflater inflater, ViewGroup parent) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.promoted_group, parent, false);
    return new PromotedGroupViewHolder(view, promotedItemsClickListener, imageLoader,
        numberFormatUtil, changesListener);
  }

  @Override public void bind(PromotedGroupViewHolder holder, PromotedGroupModel item) {
    holder.render(item, positionInList, offsetInList);
  }

  @Override public boolean canBindData(Object item) {
    return item instanceof PromotedGroupModel;
  }

  public interface ChangesListener {
    void savePosition(int position, int offset);
  }
}
