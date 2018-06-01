package com.shootr.mobile.ui.adapters.streamtimeline;

import android.os.Bundle;
import android.view.View;
import com.ahamed.multiviewadapter.DataItemManager;
import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.RecyclerAdapter;
import com.ahamed.multiviewadapter.util.PayloadProvider;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnCtaClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnDeleteHighlightedItemClick;
import com.shootr.mobile.ui.adapters.listeners.OnImageClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageLongClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnItemsInserted;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnOpenShotMenuListener;
import com.shootr.mobile.ui.adapters.listeners.OnPollActionClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnReshootClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.adapters.streamtimeline.holder.BubbleShotBinder;
import com.shootr.mobile.ui.adapters.streamtimeline.holder.CtaShotBinder;
import com.shootr.mobile.ui.adapters.streamtimeline.holder.FixedCtaShotBinder;
import com.shootr.mobile.ui.adapters.streamtimeline.holder.FixedShotBinder;
import com.shootr.mobile.ui.adapters.streamtimeline.holder.LoadingBinder;
import com.shootr.mobile.ui.adapters.streamtimeline.holder.MyShotBinder;
import com.shootr.mobile.ui.adapters.streamtimeline.holder.PollBinder;
import com.shootr.mobile.ui.adapters.streamtimeline.holder.TopicBinder;
import com.shootr.mobile.ui.model.PrintableModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShotTextSpannableBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StreamTimelineAdapter extends RecyclerAdapter {

  private final ImageLoader imageLoader;
  private final OnAvatarClickListener avatarClickListener;
  private final OnVideoClickListener videoClickListener;
  private final OnNiceShotListener onNiceShotListener;
  private final OnUsernameClickListener onUsernameClickListener;
  private final AndroidTimeUtils timeUtils;
  private final ShotTextSpannableBuilder shotTextSpannableBuilder;
  private final ShotClickListener shotClickListener;
  private final OnShotLongClick onShotLongClick;
  private final OnOpenShotMenuListener onOpenShotMenuListener;
  private final OnImageLongClickListener onLongClickListener;
  private final View.OnTouchListener onTouchListener;
  private final OnImageClickListener onImageClickListener;
  private final OnDeleteHighlightedItemClick onHideHighlightClickListener;
  private final OnUrlClickListener onUrlClickListener;
  private final OnUrlClickListener onUrlFixedShotClickListener;
  private final OnReshootClickListener onReshootClickListener;
  private final OnCtaClickListener onCtaClickListener;
  private final OnItemsInserted onItemsInserted;
  private final NumberFormatUtil numberFormatUtil;
  private final View.OnClickListener onTopicClick;
  private final OnPollActionClickListener onPollActionClickListener;
  private final boolean canFixItems;

  private DataListManager<PrintableModel> shotsListManager;
  private DataListManager<PrintableModel> fixedItemsListManager;
  private DataListManager<PrintableModel> pinnedItemsListManager;
  private List<PrintableModel> nicestList;

  private DataItemManager<Boolean> loadingItem;

  public StreamTimelineAdapter(ImageLoader imageLoader, OnAvatarClickListener avatarClickListener,
      OnVideoClickListener videoClickListener, OnNiceShotListener onNiceShotListener,
      OnUsernameClickListener onUsernameClickListener, AndroidTimeUtils timeUtils,
      ShotTextSpannableBuilder shotTextSpannableBuilder, ShotClickListener shotClickListener,
      OnShotLongClick onShotLongClick, OnOpenShotMenuListener onOpenShotMenuListener,
      OnImageLongClickListener onLongClickListener, View.OnTouchListener onTouchListener,
      OnImageClickListener onImageClickListener,
      OnDeleteHighlightedItemClick onHideHighlightClickListener,
      OnUrlClickListener onUrlClickListener, OnUrlClickListener onUrlFixedShotClickListener,
      OnReshootClickListener onReshootClickListener, OnCtaClickListener onCtaClickListener,
      OnItemsInserted onItemsInserted, NumberFormatUtil numberFormatUtil,
      View.OnClickListener onTopicClick, OnPollActionClickListener onPollActionClickListener,
      boolean canFixItems) {
    this.imageLoader = imageLoader;
    this.avatarClickListener = avatarClickListener;
    this.videoClickListener = videoClickListener;
    this.onNiceShotListener = onNiceShotListener;
    this.onUsernameClickListener = onUsernameClickListener;
    this.timeUtils = timeUtils;
    this.shotTextSpannableBuilder = shotTextSpannableBuilder;
    this.shotClickListener = shotClickListener;
    this.onShotLongClick = onShotLongClick;
    this.onOpenShotMenuListener = onOpenShotMenuListener;
    this.onLongClickListener = onLongClickListener;
    this.onTouchListener = onTouchListener;
    this.onImageClickListener = onImageClickListener;
    this.onHideHighlightClickListener = onHideHighlightClickListener;
    this.onUrlClickListener = onUrlClickListener;
    this.onUrlFixedShotClickListener = onUrlFixedShotClickListener;
    this.onReshootClickListener = onReshootClickListener;
    this.onCtaClickListener = onCtaClickListener;
    this.onItemsInserted = onItemsInserted;
    this.numberFormatUtil = numberFormatUtil;
    this.onTopicClick = onTopicClick;
    this.onPollActionClickListener = onPollActionClickListener;
    this.canFixItems = canFixItems;

    setupList();
  }

  private void setupList() {
    nicestList = new ArrayList<>();
    shotsListManager = new DataListManager<>(this, new PayloadProvider<PrintableModel>() {
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
    });
    fixedItemsListManager = new DataListManager<>(this, new PayloadProvider<PrintableModel>() {
      @Override public boolean areContentsTheSame(PrintableModel oldItem, PrintableModel newItem) {
        if (oldItem instanceof ShotModel && newItem instanceof ShotModel) {
          return isTheSameItem((ShotModel) oldItem, (ShotModel) newItem);
        }
        return false;
      }

      @Override public Object getChangePayload(PrintableModel oldItem, PrintableModel newItem) {
        return null;
      }
    });
    pinnedItemsListManager = new DataListManager<>(this);

    addDataManager(pinnedItemsListManager);
    addDataManager(fixedItemsListManager);
    addDataManager(shotsListManager);

    registerBinder(
        new BubbleShotBinder(imageLoader, avatarClickListener, videoClickListener, onNiceShotListener,
            onUsernameClickListener, timeUtils, shotTextSpannableBuilder, shotClickListener,
            onShotLongClick, onOpenShotMenuListener, onLongClickListener, onTouchListener,
            onImageClickListener, onHideHighlightClickListener, onUrlClickListener,
            onReshootClickListener, onCtaClickListener, numberFormatUtil));

    registerBinder(
        new MyShotBinder(imageLoader, avatarClickListener, videoClickListener, onNiceShotListener,
            onUsernameClickListener, timeUtils, shotTextSpannableBuilder, shotClickListener,
            onShotLongClick, onOpenShotMenuListener, onLongClickListener, onTouchListener,
            onImageClickListener, onHideHighlightClickListener, onUrlClickListener,
            onReshootClickListener, onCtaClickListener, numberFormatUtil));

    registerBinder(new FixedShotBinder(imageLoader, avatarClickListener, videoClickListener,
        onNiceShotListener, onUsernameClickListener, timeUtils, shotTextSpannableBuilder,
        shotClickListener, onShotLongClick, onOpenShotMenuListener, onLongClickListener,
        onTouchListener, onImageClickListener, onHideHighlightClickListener,
        onUrlFixedShotClickListener, onReshootClickListener, onCtaClickListener, numberFormatUtil,
        canFixItems));

    registerBinder(
        new CtaShotBinder(imageLoader, avatarClickListener, videoClickListener, onNiceShotListener,
            onUsernameClickListener, timeUtils, shotTextSpannableBuilder, shotClickListener,
            onShotLongClick, onOpenShotMenuListener, onLongClickListener, onTouchListener,
            onImageClickListener, onHideHighlightClickListener, onUrlClickListener,
            onReshootClickListener, onCtaClickListener, numberFormatUtil));

    registerBinder(new FixedCtaShotBinder(imageLoader, avatarClickListener, videoClickListener,
        onNiceShotListener, onUsernameClickListener, timeUtils, shotTextSpannableBuilder,
        shotClickListener, onShotLongClick, onOpenShotMenuListener, onLongClickListener,
        onTouchListener, onImageClickListener, onHideHighlightClickListener,
        onUrlFixedShotClickListener, onReshootClickListener, onCtaClickListener, numberFormatUtil,
        canFixItems));

    registerBinder(new TopicBinder(onTopicClick));

    registerBinder(new PollBinder(onPollActionClickListener));

    registerBinder(new LoadingBinder());
  }

  private boolean isTheSameItem(ShotModel oldItem, ShotModel newItem) {
    return oldItem.getIdShot().equals(newItem.getIdShot())
        && oldItem.getNiceCount().equals(newItem.getNiceCount())
        && oldItem.getReplyCount().equals(newItem.getReplyCount())
        && newItem.isReshooted() == oldItem.isReshooted()
        && oldItem.isNiced() == newItem.isNiced();
  }

  public void setShotList(List<PrintableModel> list) {
    shotsListManager.set(list);
  }

  public void setNicestShotList(List<PrintableModel> list) {
    Collections.sort(list, new ShotModel.OrderFieldComparator());
    nicestList = list;
    shotsListManager.set(nicestList);
  }

  public void setFixedItems(List<PrintableModel> list) {
    fixedItemsListManager.set(list);
  }

  public void setPinnedItems(List<PrintableModel> items) {
    pinnedItemsListManager.set(items);
  }

  public void addNewItems(List<PrintableModel> list) {
    shotsListManager.addAll(0, list);
    onItemsInserted.onInserted(list.size());
  }

  public void addOldItems(List<PrintableModel> list) {
    shotsListManager.addAll(shotsListManager.getCount(), list);
  }

  public void showLoadingMoreItems() {
    if (loadingItem == null) {
      loadingItem = new DataItemManager<>(this, false);
      addDataManager(loadingItem);
    }
    loadingItem.setItem(true);
  }

  public void hideLoadingMoreItems() {
    if (loadingItem != null) {
      loadingItem.setItem(false);
    }
  }

  public void addNewItem(PrintableModel item) {
    item.setTimelineGroup(PrintableModel.ITEMS_GROUP);
    shotsListManager.add(0, item);
    onItemsInserted.onInserted(1);
  }

  public void updateItem(PrintableModel item) {
    if (item.getTimelineGroup().equals(PrintableModel.ITEMS_GROUP)) {
      int index = shotsListManager.indexOf(item);
      if (index != -1) {
        if (((ShotModel) item).isDeleted()) {
          shotsListManager.remove(index);
        } else {
          shotsListManager.set(index, item);
        }
      }
    }
  }

  public void updateNicestItem(PrintableModel item) {
    if (item.getTimelineGroup().equals(PrintableModel.ITEMS_GROUP)) {
      int index = nicestList.indexOf(item);
      if (index != -1) {
        if (((ShotModel) item).isDeleted()) {
          nicestList.remove(index);
        } else {
          nicestList.set(index, item);
        }
      } else {
        nicestList.add(item);
      }
      setNicestShotList(nicestList);
    }
  }

  public void handleNicestItem(PrintableModel item) {
    if (item.getTimelineGroup().equals(PrintableModel.ITEMS_GROUP)) {
      nicestList.remove(item);
      setNicestShotList(nicestList);
    }
  }

  public void clearItems() {
    shotsListManager.clear();
    fixedItemsListManager.clear();
    pinnedItemsListManager.clear();
  }

  public void removeHighlightedItems() {
    fixedItemsListManager.clear();
  }

  public void updateFixedItem(PrintableModel item) {
    int index = fixedItemsListManager.indexOf(item);
    if (index != -1) {
      if (((ShotModel) item).isDeleted()) {
        fixedItemsListManager.remove(index);
      } else {
        fixedItemsListManager.set(index, item);
      }
    }
  }

  public PrintableModel itemForIndex(int index) {
    return shotsListManager.get(index);
  }

  public int indexOf(PrintableModel printableModel) {
    return shotsListManager.lastIndexOf(printableModel);
  }
}
