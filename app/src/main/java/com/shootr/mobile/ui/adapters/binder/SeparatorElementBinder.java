package com.shootr.mobile.ui.adapters.binder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ahamed.multiviewadapter.BaseViewHolder;
import com.ahamed.multiviewadapter.ItemBinder;
import com.shootr.mobile.R;

public class SeparatorElementBinder extends ItemBinder<ListElement, SeparatorElementBinder.SeparatorViewHolder> {

  @Override public SeparatorViewHolder create(LayoutInflater inflater, ViewGroup parent) {
    return new SeparatorViewHolder(inflater.inflate(R.layout.hot_separator, parent, false));
  }

  @Override public void bind(SeparatorViewHolder holder, ListElement item) {
    /* no-op */
  }

  @Override public boolean canBindData(Object item) {
    if (item instanceof ListElement) {
      return ((ListElement) item).getElementType().equals(ListElement.SEPARATOR);
    }
    return false;
  }

  static class SeparatorViewHolder extends BaseViewHolder<ListElement> {

    SeparatorViewHolder(View itemView) {
      super(itemView);
    }
  }
}
