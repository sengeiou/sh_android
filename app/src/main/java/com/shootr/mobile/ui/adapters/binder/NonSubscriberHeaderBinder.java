package com.shootr.mobile.ui.adapters.binder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ahamed.multiviewadapter.BaseViewHolder;
import com.ahamed.multiviewadapter.ItemBinder;
import com.shootr.mobile.R;

public class NonSubscriberHeaderBinder
    extends ItemBinder<ListElement, NonSubscriberHeaderBinder.NonSubcriberHeaderViewHolder> {

  @Override public NonSubcriberHeaderViewHolder create(LayoutInflater inflater, ViewGroup parent) {
    return new NonSubcriberHeaderViewHolder(
        inflater.inflate(R.layout.non_subscribers_header, parent, false));
  }

  @Override public void bind(NonSubcriberHeaderViewHolder holder, ListElement item) {
    /* no-op */
  }

  @Override public boolean canBindData(Object item) {
    return item instanceof ListElement && ((ListElement) item).getElementType()
        .equals(ListElement.NON_SUBSCRIBERS);
  }

  static class NonSubcriberHeaderViewHolder extends BaseViewHolder<ListElement> {

    NonSubcriberHeaderViewHolder(View itemView) {
      super(itemView);
    }
  }
}
