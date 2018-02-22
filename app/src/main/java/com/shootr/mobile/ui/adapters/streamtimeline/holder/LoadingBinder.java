package com.shootr.mobile.ui.adapters.streamtimeline.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.ahamed.multiviewadapter.BaseViewHolder;
import com.ahamed.multiviewadapter.ItemBinder;
import com.shootr.mobile.R;

public class LoadingBinder extends ItemBinder<Boolean, LoadingBinder.LoadingViewHolder> {

  @Override public LoadingViewHolder create(LayoutInflater inflater, ViewGroup parent) {
    View view = inflater.inflate(R.layout.item_loading, parent, false);
    return new LoadingViewHolder(view);
  }

  @Override public void bind(LoadingViewHolder holder, Boolean show) {
    holder.loading.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override public boolean canBindData(Object item) {
    return item instanceof Boolean;
  }

  public static class LoadingViewHolder extends BaseViewHolder<Boolean> {

    ProgressBar loading;

    public LoadingViewHolder(View itemView) {
      super(itemView);
      loading = (ProgressBar) itemView.findViewById(R.id.loading);
    }
  }
}
