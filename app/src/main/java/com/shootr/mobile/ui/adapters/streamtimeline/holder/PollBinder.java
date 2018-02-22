package com.shootr.mobile.ui.adapters.streamtimeline.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ahamed.multiviewadapter.ItemBinder;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnPollActionClickListener;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PrintableModel;

public class PollBinder extends ItemBinder<PollModel, PollViewHolder> {

  private final OnPollActionClickListener onPollActionClickListener;

  public PollBinder(OnPollActionClickListener onPollActionClickListener) {
    this.onPollActionClickListener = onPollActionClickListener;
  }

  @Override public PollViewHolder create(LayoutInflater inflater, ViewGroup parent) {
    View view = inflater.inflate(R.layout.item_timeline_pinned_poll, parent, false);
    return new PollViewHolder(view, onPollActionClickListener);
  }

  @Override public void bind(PollViewHolder holder, PollModel item) {
    holder.render(item);
  }

  @Override public boolean canBindData(Object item) {
    return item instanceof PollModel && ((PollModel) item).getTimelineGroup().equals(
        PrintableModel.PINNED_GROUP);
  }
}
