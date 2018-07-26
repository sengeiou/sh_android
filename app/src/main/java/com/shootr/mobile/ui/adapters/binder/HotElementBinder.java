package com.shootr.mobile.ui.adapters.binder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ahamed.multiviewadapter.BaseViewHolder;
import com.ahamed.multiviewadapter.ItemBinder;
import com.shootr.mobile.R;

public class HotElementBinder extends ItemBinder<ListElement, HotElementBinder.HotHeaderViewHolder> {

  @Override public HotHeaderViewHolder create(LayoutInflater inflater, ViewGroup parent) {
    return new HotHeaderViewHolder(inflater.inflate(R.layout.hot_header, parent, false));
  }

  @Override public void bind(HotHeaderViewHolder holder, ListElement item) {
    /* no-op */
  }

  @Override public boolean canBindData(Object item) {
    if (item instanceof ListElement) {
      return ((ListElement) item).getElementType().equals(ListElement.HEADER);
    }
    return false;
  }

  static class HotHeaderViewHolder extends BaseViewHolder<ListElement> {

    HotHeaderViewHolder(View itemView) {
      super(itemView);
    }
  }
}
