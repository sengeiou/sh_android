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

public class TimelineHighlightedShotBinder extends ItemBinder<ShotModel, TimelineHighlightedShotViewHolder> {

  private final PromotedItemClickListener promotedItemClickListener;
  private final OnShotLongClick onShotLongClick;
  private final ImageLoader imageLoader;

  public TimelineHighlightedShotBinder(PromotedItemClickListener promotedItemClickListener,
      OnShotLongClick onShotLongClick, ImageLoader imageLoader) {
    this.promotedItemClickListener = promotedItemClickListener;
    this.onShotLongClick = onShotLongClick;
    this.imageLoader = imageLoader;
  }

  @Override
  public TimelineHighlightedShotViewHolder create(LayoutInflater inflater, ViewGroup parent) {
    View view = inflater.inflate(R.layout.item_timeline_highlighted_shot, parent, false);
    return new TimelineHighlightedShotViewHolder(view, promotedItemClickListener, onShotLongClick,
        imageLoader);
  }

  @Override public void bind(TimelineHighlightedShotViewHolder holder, ShotModel item) {
    holder.render(item);
  }

  @Override public boolean canBindData(Object item) {
    return item instanceof ShotModel && ((ShotModel) item).getTimelineGroup()
        .equals(PrintableModel.HIGHLIGHTED_GROUP);
  }

  @Override
  public void bind(TimelineHighlightedShotViewHolder holder, ShotModel item, List payloads) {
    Bundle bundle = (Bundle) payloads.get(0);

    for (String key : bundle.keySet()) {
      switch (key) {
        case PromotedItemsAdapter.SEEN:
          holder.setupSeenMark((ShotModel) bundle.getSerializable(PromotedItemsAdapter.SEEN));
          break;
        default:
          break;
      }
    }
  }
}
