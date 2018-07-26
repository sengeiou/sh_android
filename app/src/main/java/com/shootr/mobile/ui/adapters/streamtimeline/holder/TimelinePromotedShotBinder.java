package com.shootr.mobile.ui.adapters.streamtimeline.holder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ahamed.multiviewadapter.ItemBinder;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.PromotedItemClickListener;
import com.shootr.mobile.ui.adapters.streamtimeline.PromotedItemsAdapter;
import com.shootr.mobile.ui.model.PrintableModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.ImageLoader;
import java.util.List;

public class TimelinePromotedShotBinder extends ItemBinder<ShotModel, TimelinePromotedShotViewHolder> {

  private final PromotedItemClickListener promotedItemClickListener;
  private final ImageLoader imageLoader;
  private final OnShotLongClick onShotLongClick;

  public TimelinePromotedShotBinder(PromotedItemClickListener promotedItemClickListener,
      ImageLoader imageLoader, OnShotLongClick onShotLongClick) {
    this.promotedItemClickListener = promotedItemClickListener;
    this.imageLoader = imageLoader;
    this.onShotLongClick = onShotLongClick;
  }

  @Override
  public TimelinePromotedShotViewHolder create(LayoutInflater inflater, ViewGroup parent) {
    View view = inflater.inflate(R.layout.item_timeline_promoted_shot, parent, false);
    return new TimelinePromotedShotViewHolder(view, promotedItemClickListener, imageLoader,
        onShotLongClick);
  }

  @Override public void bind(TimelinePromotedShotViewHolder holder, ShotModel item) {
    holder.render(item);
  }

  @Override public boolean canBindData(Object item) {
    return item instanceof ShotModel && ((ShotModel) item).getTimelineGroup()
        .equals(PrintableModel.PROMOTED_GROUP);
  }

  @Override
  public void bind(TimelinePromotedShotViewHolder holder, ShotModel item, List payloads) {
    Bundle bundle = (Bundle) payloads.get(0);

    for (String key : bundle.keySet()) {
      switch (key) {
        case PromotedItemsAdapter.SEEN:
          holder.setupSeen((ShotModel) bundle.getSerializable(PromotedItemsAdapter.SEEN));
          break;
        default:
          break;
      }
    }
  }
}
