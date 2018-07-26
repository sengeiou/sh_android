package com.shootr.mobile.ui.adapters.streamtimeline;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.ahamed.multiviewadapter.DataItemManager;
import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.RecyclerAdapter;
import com.ahamed.multiviewadapter.util.PayloadProvider;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.PromotedItemClickListener;
import com.shootr.mobile.ui.adapters.streamtimeline.holder.AddPromotedBinder;
import com.shootr.mobile.ui.adapters.streamtimeline.holder.TimelineHighlightedShotBinder;
import com.shootr.mobile.ui.adapters.streamtimeline.holder.TimelinePollBinder;
import com.shootr.mobile.ui.adapters.streamtimeline.holder.TimelinePromotedShotBinder;
import com.shootr.mobile.ui.adapters.streamtimeline.holder.TimelineUserFollowingBinder;
import com.shootr.mobile.ui.model.PrintableModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.util.ImageLoader;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PromotedItemsAdapter extends RecyclerAdapter {

  public static final String SEEN = "SEEN";

  private final PromotedItemClickListener promotedItemClickListener;
  private final OnShotLongClick onShotLongClick;
  private final ImageLoader imageLoader;

  private DataListManager<PrintableModel> promotedItems;
  private DataListManager<PrintableModel> highlightedItems;
  private DataListManager<PrintableModel> pollItems;
  private DataListManager<PrintableModel> followingItems;
  private DataItemManager<Boolean> addPromoted;
  private List<PrintableModel> promotedList;
  private List<PrintableModel> userList;
  private boolean shouldShowAddSuperShot = false;


  public PromotedItemsAdapter(PromotedItemClickListener promotedItemClickListener,
      OnShotLongClick onShotLongClick, ImageLoader imageLoader) {
    this.promotedItemClickListener = promotedItemClickListener;
    this.onShotLongClick = onShotLongClick;
    this.imageLoader = imageLoader;
    setupList();
  }

  private void setupList() {
    promotedItems = new DataListManager<>(this, getPayloadProvider());
    highlightedItems = new DataListManager<>(this, getPayloadProvider());
    pollItems = new DataListManager<>(this, new PayloadProvider<PrintableModel>() {
      @Override public boolean areContentsTheSame(PrintableModel oldItem, PrintableModel newItem) {
        return false;
      }

      @Override public Object getChangePayload(PrintableModel oldItem, PrintableModel newItem) {
        return null;
      }
    });
    followingItems = new DataListManager<>(this, getUserPayloadProvider());
    addPromoted = new DataItemManager<Boolean>(this);

    addDataManager(pollItems);
    addDataManager(highlightedItems);
    addDataManager(addPromoted);
    addDataManager(promotedItems);
    addDataManager(followingItems);

    registerBinder(new TimelinePollBinder(promotedItemClickListener, imageLoader));
    registerBinder(
        new TimelineHighlightedShotBinder(promotedItemClickListener, onShotLongClick, imageLoader));
    registerBinder(
        new TimelinePromotedShotBinder(promotedItemClickListener, imageLoader, onShotLongClick));
    registerBinder(new TimelineUserFollowingBinder(promotedItemClickListener, imageLoader));
    registerBinder(new AddPromotedBinder(promotedItemClickListener, imageLoader));
  }

  public void setPromotedList(List<PrintableModel> promoteds) {
    setupAddPromoted(promoteds);
    promotedList = promoteds;
    Collections.sort(promotedList, new ShotModel.OrderFieldPromotedComparator());
    promotedItems.set(promotedList);
  }

  private void setupAddPromoted(List<PrintableModel> promoteds) {
    if (shouldShowAddSuperShot) {
      if (promoteds.isEmpty()) {
        addPromoted.setItem(true);
      } else {
        addPromoted.removeItem();
      }
    }
  }

  public void showAddPromoted() {
    addPromoted.setItem(true);
  }

  public void setHighlightedList(List<PrintableModel> highlightedList) {
    highlightedItems.set(highlightedList);
  }

  public void setFollowingList(List<PrintableModel> users) {
    this.userList = users;
    Collections.sort(userList, new UserModel.OrderFieldComparator());
    followingItems.set(userList);
  }

  public void setPolls(List<PrintableModel> polls) {
    pollItems.set(polls);
  }

  public void addNewHighlighted(List<PrintableModel> printableModels) {
    highlightedItems.addAll(0, printableModels);
  }

  public void addNewPoll(List<PrintableModel> printableModels) {
    pollItems.addAll(0, printableModels);
  }

  public void addNewPromoted(List<PrintableModel> printableModels) {
    promotedList.addAll(printableModels);
    Collections.sort(promotedList, new ShotModel.OrderFieldPromotedComparator());
    setPromotedList(promotedList);
  }

  public void addNewUser(List<PrintableModel> printableModels) {
    userList.addAll(printableModels);
    Collections.sort(userList, new UserModel.OrderFieldComparator());
    setFollowingList(userList);
  }

  public void updateHighlighted(PrintableModel item) {
    int index = highlightedItems.indexOf(item);
    if (index != -1) {
      if (item.isDeleted()) {
        highlightedItems.remove(index);
      } else {
        highlightedItems.set(index, item);
      }
    }
  }

  public void updatePolls(PrintableModel item) {
    int index = pollItems.indexOf(item);
    if (index != -1) {
      if (item.isDeleted()) {
        pollItems.remove(index);
      } else {
        pollItems.set(index, item);
      }
    }
  }

  public void updatePromoted(PrintableModel item) {
    int index = promotedList.indexOf(item);
    if (index != -1) {
      if (item.isDeleted()) {
        promotedList.remove(index);
      } else {
        promotedList.set(index, item);
      }
      setPromotedList(promotedList);
    }
  }

  public void updateFollowing(PrintableModel item) {
    int index = userList.indexOf(item);
    if (index != -1) {
      if (item.isDeleted()) {
        userList.remove(index);
      } else {
        userList.set(index, item);
      }
      setFollowingList(userList);
    }
  }

  public void setShouldShowAddSuperShot(boolean shoudlShow) {
    this.shouldShowAddSuperShot = shoudlShow;
  }

  @NonNull private PayloadProvider<PrintableModel> getPayloadProvider() {
    return new PayloadProvider<PrintableModel>() {
      @Override public boolean areContentsTheSame(PrintableModel oldItem, PrintableModel newItem) {
        return oldItem instanceof ShotModel && newItem instanceof ShotModel && isTheSameItem(
            (ShotModel) oldItem, (ShotModel) newItem);
      }

      @Override public Object getChangePayload(PrintableModel oldItem, PrintableModel newItem) {
        Bundle diffBundle = new Bundle();
        if (!((ShotModel) oldItem).getSeen().equals(((ShotModel) newItem).getSeen())) {
          diffBundle.putSerializable(SEEN, (ShotModel) newItem);
        }

        return diffBundle;
      }
    };
  }

  private boolean isTheSameItem(ShotModel oldItem, ShotModel newItem) {
    return oldItem.getIdShot().equals(newItem.getIdShot())
        && oldItem.getNiceCount().equals(newItem.getNiceCount())
        && oldItem.getReplyCount().equals(newItem.getReplyCount())
        && newItem.isReshooted() == oldItem.isReshooted()
        && newItem.getSeen() == oldItem.getSeen()
        && Objects.equals(newItem.getOrder(), oldItem.getOrder())
        && oldItem.isNiced() == newItem.isNiced();
  }

  @NonNull private PayloadProvider<PrintableModel> getUserPayloadProvider() {
    return new PayloadProvider<PrintableModel>() {
      @Override public boolean areContentsTheSame(PrintableModel oldItem, PrintableModel newItem) {
        return oldItem instanceof UserModel && newItem instanceof UserModel && isTheSameItem(
            (UserModel) oldItem, (UserModel) newItem);
      }

      @Override public Object getChangePayload(PrintableModel oldItem, PrintableModel newItem) {
        return null;
      }
    };
  }

  private boolean isTheSameItem(UserModel oldItem, UserModel newItem) {
    return oldItem.getIdUser().equals(newItem.getIdUser())
        && oldItem.getName()
        .equals(newItem.getName())
        && oldItem.getSeen().equals(newItem.getSeen())
        && Objects.equals(oldItem.getPhoto(), newItem.getPhoto())
        && Objects.equals(newItem.getOrder(), oldItem.getOrder());
  }

}
