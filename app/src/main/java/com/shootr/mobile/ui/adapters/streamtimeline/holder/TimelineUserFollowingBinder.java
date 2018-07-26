package com.shootr.mobile.ui.adapters.streamtimeline.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ahamed.multiviewadapter.ItemBinder;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.PromotedItemClickListener;
import com.shootr.mobile.ui.model.PrintableModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.util.ImageLoader;

public class TimelineUserFollowingBinder extends ItemBinder<UserModel, TimelineFollowingUserViewHolder> {

  private final PromotedItemClickListener promotedItemClickListener;
  private final ImageLoader imageLoader;

  public TimelineUserFollowingBinder(PromotedItemClickListener promotedItemClickListener, ImageLoader imageLoader) {
    this.promotedItemClickListener = promotedItemClickListener;
    this.imageLoader = imageLoader;
  }

  @Override
  public TimelineFollowingUserViewHolder create(LayoutInflater inflater, ViewGroup parent) {
    View view = inflater.inflate(R.layout.item_timeline_following, parent, false);
    return new TimelineFollowingUserViewHolder(view, promotedItemClickListener, imageLoader);
  }

  @Override public void bind(TimelineFollowingUserViewHolder holder, UserModel item) {
    holder.render(item);
  }

  @Override public boolean canBindData(Object item) {
    return item instanceof UserModel && ((UserModel) item).getTimelineGroup()
        .equals(PrintableModel.USER_GROUP);
  }
}
