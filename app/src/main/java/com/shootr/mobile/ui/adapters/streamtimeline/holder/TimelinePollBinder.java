package com.shootr.mobile.ui.adapters.streamtimeline.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ahamed.multiviewadapter.ItemBinder;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.PromotedItemClickListener;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PrintableModel;
import com.shootr.mobile.util.ImageLoader;

public class TimelinePollBinder extends ItemBinder<PollModel, TimelinePollViewHolder> {

  private final PromotedItemClickListener promotedItemClickListener;
  private final ImageLoader imageLoader;

  public TimelinePollBinder(PromotedItemClickListener promotedItemClickListener, ImageLoader imageLoader) {
    this.promotedItemClickListener = promotedItemClickListener;
    this.imageLoader = imageLoader;
  }

  @Override
  public TimelinePollViewHolder create(LayoutInflater inflater, ViewGroup parent) {
    View view = inflater.inflate(R.layout.item_timeline_poll, parent, false);
    return new TimelinePollViewHolder(view, promotedItemClickListener, imageLoader);
  }

  @Override public void bind(TimelinePollViewHolder holder, PollModel item) {
    holder.render(item);
  }

  @Override public boolean canBindData(Object item) {
    return item instanceof PollModel && ((PollModel) item).getTimelineGroup()
        .equals(PrintableModel.POLL_GROUP);
  }
}
