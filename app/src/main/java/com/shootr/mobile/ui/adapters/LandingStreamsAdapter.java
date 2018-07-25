package com.shootr.mobile.ui.adapters;

import com.ahamed.multiviewadapter.BaseViewHolder;
import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.RecyclerAdapter;
import com.ahamed.multiviewadapter.util.PayloadProvider;
import com.shootr.mobile.ui.adapters.binder.HotElementBinder;
import com.shootr.mobile.ui.adapters.binder.ListElement;
import com.shootr.mobile.ui.adapters.binder.PromotedGroupBinder;
import com.shootr.mobile.ui.adapters.binder.SeparatorElementBinder;
import com.shootr.mobile.ui.adapters.binder.StreamBinder;
import com.shootr.mobile.ui.adapters.holders.PromotedGroupViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnLandingStreamClickListener;
import com.shootr.mobile.ui.adapters.listeners.PromotedItemsClickListener;
import com.shootr.mobile.ui.model.LandingStreamsModel;
import com.shootr.mobile.ui.model.PromotedGroupModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.NumberFormatUtil;
import java.util.Collections;

public class LandingStreamsAdapter extends RecyclerAdapter {

  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;
  private final OnLandingStreamClickListener onStreamClickListener;
  private NumberFormatUtil numberFormatUtil;
  private final PromotedItemsClickListener promotedItemsClickListener;

  private DataListManager<StreamModel> userStreams;
  private DataListManager<StreamModel> hotStreams;
  private DataListManager<ListElement> separator;
  private DataListManager<ListElement> header;
  private DataListManager<PromotedGroupModel> promotedGroup;
  private PayloadProvider<StreamModel> streamPayloadProvider;

  public LandingStreamsAdapter(ImageLoader imageLoader, InitialsLoader initialsLoader,
      OnLandingStreamClickListener onStreamClickListener, NumberFormatUtil numberFormatUtil,
      PromotedItemsClickListener promotedItemsClickListener) {
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    this.onStreamClickListener = onStreamClickListener;
    this.numberFormatUtil = numberFormatUtil;
    this.promotedItemsClickListener = promotedItemsClickListener;

    setupList();
  }

  private void setupList() {

    streamPayloadProvider = new PayloadProvider<StreamModel>() {
      @Override public boolean areContentsTheSame(StreamModel oldItem, StreamModel newItem) {
        return oldItem.equals(newItem);
      }

      @Override public Object getChangePayload(StreamModel oldItem, StreamModel newItem) {
        return null;
      }
    };

    userStreams = new DataListManager<>(this, streamPayloadProvider);
    hotStreams = new DataListManager<>(this, streamPayloadProvider);
    separator = new DataListManager<>(this);
    header = new DataListManager<>(this);
    promotedGroup = new DataListManager<>(this, new PayloadProvider<PromotedGroupModel>() {
      @Override
      public boolean areContentsTheSame(PromotedGroupModel oldItem, PromotedGroupModel newItem) {
        return oldItem.equals(newItem);
      }

      @Override
      public Object getChangePayload(PromotedGroupModel oldItem, PromotedGroupModel newItem) {
        return null;
      }
    });

    addDataManager(promotedGroup);
    addDataManager(userStreams);
    addDataManager(separator);
    addDataManager(header);
    addDataManager(hotStreams);

    registerBinder(
        new StreamBinder(imageLoader, initialsLoader, onStreamClickListener, numberFormatUtil));
    registerBinder(new SeparatorElementBinder());
    registerBinder(new HotElementBinder());
    registerBinder(
        new PromotedGroupBinder(promotedItemsClickListener, imageLoader, numberFormatUtil));
  }

  public void setPromoted(PromotedGroupModel promoted) {
    if (promoted.getPromotedModels().isEmpty()) {
      promotedGroup.clear();
    } else {
      promotedGroup.set(Collections.singletonList(promoted));
    }
  }

  public void setStreams(LandingStreamsModel landingStreams) {
    header.clear();
    hotStreams.clear();
    userStreams.clear();
    separator.clear();

    hotStreams.set(landingStreams.getHotStreams());
    header.set(Collections.singletonList(new ListElement(ListElement.HEADER)));
    userStreams.set(landingStreams.getUserStreams());
    if (!userStreams.isEmpty()) {
      separator.set(Collections.singletonList(new ListElement(ListElement.SEPARATOR)));
    }
  }

  public void onFollow(StreamModel stream) {
    if (hotStreams.contains(stream)) {
      hotStreams.set(hotStreams.indexOf(stream), stream);
    }

    if (userStreams.contains(stream)) {
      userStreams.set(userStreams.indexOf(stream), stream);
    }
  }

  public void onMute(StreamModel stream) {
    if (hotStreams.contains(stream)) {
      hotStreams.set(hotStreams.indexOf(stream), stream);
    }

    if (userStreams.contains(stream)) {
      userStreams.set(userStreams.indexOf(stream), stream);
    }
  }

  public void onHide(StreamModel stream) {
    if (userStreams.contains(stream)) {
      userStreams.remove(stream);
      notifyDataSetChanged();
    }
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public void onViewRecycled(BaseViewHolder holder) {
    if (holder instanceof PromotedGroupViewHolder) {
      ((PromotedGroupViewHolder) holder).saveLastPosition();
    }
    super.onViewRecycled(holder);
  }
}
