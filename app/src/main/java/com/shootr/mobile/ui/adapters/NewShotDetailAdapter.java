package com.shootr.mobile.ui.adapters;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import com.ahamed.multiviewadapter.DataItemManager;
import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.RecyclerAdapter;
import com.ahamed.multiviewadapter.util.PayloadProvider;
import com.shootr.mobile.ui.adapters.binder.ListElement;
import com.shootr.mobile.ui.adapters.binder.NonSubscriberHeaderBinder;
import com.shootr.mobile.ui.adapters.binder.SeparatorElementBinder;
import com.shootr.mobile.ui.adapters.binder.ShotDetailBinder;
import com.shootr.mobile.ui.adapters.binder.ShotDetailRepliesBinder;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageLongClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnOpenShotMenuListener;
import com.shootr.mobile.ui.adapters.listeners.OnReshootClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShareClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.adapters.streamtimeline.holder.BubbleShotBinder;
import com.shootr.mobile.ui.model.PrintableModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NicerTextSpannableBuilder;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShotTextSpannableBuilder;
import com.shootr.mobile.util.TimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NewShotDetailAdapter extends RecyclerAdapter {

  private final ImageLoader imageLoader;
  private final NumberFormatUtil numberFormatUtil;
  private final OnUsernameClickListener onUsernameClickListener;
  private final OnAvatarClickListener avatarClickListener;
  private final OnVideoClickListener videoClickListener;
  private final TimeFormatter timeFormatter;
  private final Resources resources;
  private final OnNiceShotListener onNiceShotListener;
  private final OnUrlClickListener onUrlClickListener;
  private final OnReshootClickListener onReshootClickListener;
  private final ShareClickListener reshootClickListener;
  private final ShareClickListener shareClickListener;
  private final NicerTextSpannableBuilder nicerTextSpannableBuilder;
  private final OnImageClickListener imageClickListener;
  private final ShotClickListener nicesClickListener;
  private final ShotClickListener streamClickListener;
  private final ShotClickListener replyShotClickListener;
  private final AndroidTimeUtils timeUtils;
  private final ShotTextSpannableBuilder shotTextSpannableBuilder;
  private final OnShotLongClick onShotLongClick;
  private final OnOpenShotMenuListener onOpenShotMenuListener;
  private final OnImageLongClickListener onLongClickListener;
  private final View.OnTouchListener onTouchListener;

  private DataListManager<PrintableModel> parentShots;
  private DataListManager<PrintableModel> promotedReplies;
  private DataListManager<PrintableModel> subscribersReplies;
  private DataListManager<PrintableModel> basicReplies;
  private DataListManager<PrintableModel> mainShot;
  private DataItemManager<ListElement> nonSubscribersHeader;
  private PayloadProvider<PrintableModel> payloadProvider;
  private List<PrintableModel> parentsList;

  public NewShotDetailAdapter(ImageLoader imageLoader, NumberFormatUtil numberFormatUtil,
      OnUsernameClickListener onUsernameClickListener, OnAvatarClickListener avatarClickListener,
      OnVideoClickListener videoClickListener, TimeFormatter timeFormatter, Resources resources,
      OnNiceShotListener onNiceShotListener, OnUrlClickListener onUrlClickListener,
      OnReshootClickListener onReshootClickListener, ShareClickListener reshootClickListener,
      ShareClickListener shareClickListener, OnImageClickListener imageClickListener,
      ShotClickListener nicesClickListener, ShotClickListener streamClickListener,
      ShotClickListener replyShotClickListener, AndroidTimeUtils timeUtils,
      OnShotLongClick onShotLongClick, OnOpenShotMenuListener onOpenShotMenuListener,
      OnImageLongClickListener onLongClickListener, View.OnTouchListener onTouchListener) {
    this.imageLoader = imageLoader;
    this.numberFormatUtil = numberFormatUtil;
    this.onUsernameClickListener = onUsernameClickListener;
    this.avatarClickListener = avatarClickListener;
    this.videoClickListener = videoClickListener;
    this.timeFormatter = timeFormatter;
    this.resources = resources;
    this.onNiceShotListener = onNiceShotListener;
    this.onUrlClickListener = onUrlClickListener;
    this.onReshootClickListener = onReshootClickListener;
    this.reshootClickListener = reshootClickListener;
    this.shareClickListener = shareClickListener;
    this.imageClickListener = imageClickListener;
    this.nicesClickListener = nicesClickListener;
    this.streamClickListener = streamClickListener;
    this.replyShotClickListener = replyShotClickListener;
    this.timeUtils = timeUtils;
    this.onShotLongClick = onShotLongClick;
    this.onOpenShotMenuListener = onOpenShotMenuListener;
    this.onLongClickListener = onLongClickListener;
    this.onTouchListener = onTouchListener;
    this.shotTextSpannableBuilder = new ShotTextSpannableBuilder();
    this.nicerTextSpannableBuilder = new NicerTextSpannableBuilder();
    parentsList = new ArrayList<>();
    setupPayload();
    setupList();
  }

  private void setupList() {
    parentShots = new DataListManager<>(this, payloadProvider);
    promotedReplies = new DataListManager<>(this, payloadProvider);
    subscribersReplies = new DataListManager<>(this, payloadProvider);
    basicReplies = new DataListManager<>(this, payloadProvider);
    mainShot = new DataListManager<>(this, payloadProvider);
    nonSubscribersHeader = new DataItemManager<>(this);

    addDataManager(parentShots);
    addDataManager(mainShot);
    addDataManager(promotedReplies);
    addDataManager(subscribersReplies);
    addDataManager(nonSubscribersHeader);
    addDataManager(basicReplies);

    registerBinder(new ShotDetailBinder(imageLoader, avatarClickListener, videoClickListener,
        onNiceShotListener, onUsernameClickListener, timeUtils, shotTextSpannableBuilder,
        replyShotClickListener, onShotLongClick, onOpenShotMenuListener, onLongClickListener,
        onTouchListener, imageClickListener, onUrlClickListener, onReshootClickListener, null,
        numberFormatUtil, timeFormatter));
    registerBinder(new SeparatorElementBinder());
    registerBinder(new ShotDetailRepliesBinder(imageLoader, avatarClickListener, videoClickListener,
        onNiceShotListener, onUsernameClickListener, timeUtils, shotTextSpannableBuilder,
        replyShotClickListener, onShotLongClick, onOpenShotMenuListener, onLongClickListener,
        onTouchListener, imageClickListener, null, onUrlClickListener, onReshootClickListener, null,
        numberFormatUtil));
    registerBinder(new NonSubscriberHeaderBinder());
  }

  @Override public long getItemId(int position) {
    return position;
  }

  public void renderItems(List<PrintableModel> mainShot, List<PrintableModel> promotedItem,
      List<PrintableModel> subscribersItem, List<PrintableModel> basicItems,
      List<PrintableModel> parents) {
    this.mainShot.set(mainShot);
    promotedReplies.set(promotedItem);
    subscribersReplies.set(subscribersItem);
    basicReplies.set(basicItems);
    parentsList = parents;
    if ((!promotedItem.isEmpty() || !subscribersItem.isEmpty()) && !basicItems.isEmpty()) {
      showNonSubscribersHeader();
    } else {
      hideNonSubscribersHeader();
    }
  }

  public void showNonSubscribersHeader() {
    nonSubscribersHeader.setItem(new ListElement(ListElement.NON_SUBSCRIBERS));
  }

  public void hideNonSubscribersHeader() {
    nonSubscribersHeader.removeItem();
  }

  public void addPromotedShot(PrintableModel item) {
    promotedReplies.add(0, item);
  }

  public void addSubscriberShot(PrintableModel item) {
    subscribersReplies.add(0, item);
  }

  public void addOtherShot(PrintableModel item) {
    basicReplies.add(0, item);
  }

  public void updateParents(PrintableModel item) {
    updateItemInList(item, parentShots);
    updateAuxList(item);
  }

  public void updateMainShot(PrintableModel item) {
    updateItemInList(item, mainShot);
  }

  public void updatePromotedShot(PrintableModel item) {
    updateItemInList(item, promotedReplies);
  }

  public void updateSubscriberShot(PrintableModel item) {
    updateItemInList(item, subscribersReplies);
  }

  public void updateOther(PrintableModel item) {
    updateItemInList(item, basicReplies);
  }

  private void updateItemInList(PrintableModel item, DataListManager<PrintableModel> dataList) {
    int index = dataList.indexOf(item);
    if (index != -1) {
      if (((ShotModel) item).isDeleted()) {
        dataList.remove(index);
      } else {
        item.setTimelineGroup((dataList.get(index)).getTimelineGroup());
        dataList.set(index, item);
      }
    }
  }

  private void updateAuxList(PrintableModel item) {
    int index = parentsList.indexOf(item);
    if (index != -1) {
      if (((ShotModel) item).isDeleted()) {
        parentsList.remove(index);
      } else {
        item.setTimelineGroup((parentsList.get(index)).getTimelineGroup());
        parentsList.set(index, item);
      }
    }
  }

  public void showParents() {
    parentShots.set(parentsList);
  }

  public void hideParents() {
    parentShots.clear();
  }

  private boolean isTheSameItem(ShotModel oldItem, ShotModel newItem) {
    return oldItem.getIdShot().equals(newItem.getIdShot())
        && oldItem.getNiceCount().equals(newItem.getNiceCount())
        && oldItem.getReplyCount().equals(newItem.getReplyCount())
        && newItem.isReshooted() == oldItem.isReshooted()
        && oldItem.isNiced() == newItem.isNiced();
  }

  private void setupPayload() {
    payloadProvider = new PayloadProvider<PrintableModel>() {
      @Override public boolean areContentsTheSame(PrintableModel oldItem, PrintableModel newItem) {
        if (oldItem instanceof ShotModel && newItem instanceof ShotModel) {
          return isTheSameItem((ShotModel) oldItem, (ShotModel) newItem);
        }
        return false;
      }

      @Override public Object getChangePayload(PrintableModel oldItem, PrintableModel newItem) {
        Bundle diffBundle = new Bundle();
        if (!((ShotModel) oldItem).getNiceCount().equals(((ShotModel) newItem).getNiceCount())) {
          diffBundle.putSerializable(BubbleShotBinder.NICE_BUNDLE, (ShotModel) newItem);
        }

        if (!((ShotModel) oldItem).getReplyCount().equals(((ShotModel) newItem).getReplyCount())) {
          diffBundle.putSerializable(BubbleShotBinder.REPLY_BUNDLE, (ShotModel) newItem);
        }

        if (((ShotModel) oldItem).isReshooted() != (((ShotModel) newItem).isReshooted())) {
          diffBundle.putSerializable(BubbleShotBinder.RESHOOT_BUNDLE, (ShotModel) newItem);
        }

        if (((ShotModel) oldItem).hasMedia() != (((ShotModel) newItem).hasMedia())) {
          diffBundle.putSerializable(BubbleShotBinder.MEDIA_BUNDLE, (ShotModel) newItem);
        }

        return diffBundle;
      }
    };
  }

}
