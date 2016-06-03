package com.shootr.mobile.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.holders.LoadingViewHolder;
import com.shootr.mobile.ui.adapters.holders.ShotTimelineViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnHideClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnReplyShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.ShotTextSpannableBuilder;
import java.util.ArrayList;
import java.util.List;

public class ShotsTimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private final int VIEW_TYPE_ITEM = 0;
  private final int VIEW_TYPE_LOADING = 1;

  private final ImageLoader imageLoader;
  private final OnAvatarClickListener avatarClickListener;
  private final OnVideoClickListener videoClickListener;
  private final OnNiceShotListener onNiceShotListener;
  private final OnUsernameClickListener onUsernameClickListener;
  private final OnReplyShotListener onReplyShotListener;
  private final AndroidTimeUtils timeUtils;
  private final ShotTextSpannableBuilder shotTextSpannableBuilder;
  private final OnHideClickListener onHideClickListener;
  private final ShotClickListener shotClickListener;
  private final OnShotLongClick onShotLongClick;

  private List<ShotModel> shots;
  private boolean isCurrentUser;

  public ShotsTimelineAdapter(Context context, ImageLoader imageLoader, AndroidTimeUtils timeUtils,
      OnAvatarClickListener avatarClickListener, OnVideoClickListener videoClickListener,
      OnNiceShotListener onNiceShotListener, OnUsernameClickListener onUsernameClickListener,
      OnReplyShotListener onReplyShotListener, OnHideClickListener onHideClickListener,
      Boolean isCurrentUser, ShotClickListener shotClickListener, OnShotLongClick onShotLongClick) {
    this.imageLoader = imageLoader;
    this.avatarClickListener = avatarClickListener;
    this.videoClickListener = videoClickListener;
    this.onNiceShotListener = onNiceShotListener;
    this.onUsernameClickListener = onUsernameClickListener;
    this.timeUtils = timeUtils;
    this.onReplyShotListener = onReplyShotListener;
    this.shots = new ArrayList<>(0);
    this.shotTextSpannableBuilder = new ShotTextSpannableBuilder();
    this.onHideClickListener = onHideClickListener;
    this.isCurrentUser = isCurrentUser;
    this.shotClickListener = shotClickListener;
    this.onShotLongClick = onShotLongClick;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == VIEW_TYPE_ITEM) {
      View v =
          LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_shot, parent, false);
      return new ShotTimelineViewHolder(v, avatarClickListener, videoClickListener,
          onNiceShotListener, onReplyShotListener, onHideClickListener, onUsernameClickListener,
          timeUtils, imageLoader, shotTextSpannableBuilder, isCurrentUser);
    } else {
      View v =
          LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_shot, parent, false);
      return new LoadingViewHolder(v);
    }
  }

  @Override public int getItemViewType(int position) {
    return shots.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof ShotTimelineViewHolder) {
      ((ShotTimelineViewHolder) holder).render(shots.get(position), false, shotClickListener,
          onShotLongClick);
    } else if (holder instanceof LoadingViewHolder) {
      ((LoadingViewHolder) holder).render();
    }
  }

  @Override public int getItemCount() {
    return shots.size();
  }

  public void addShotsBelow(List<ShotModel> newShots) {
    this.shots.addAll(newShots);
    notifyDataSetChanged();
  }

  public void removeShot(ShotModel shotModel) {
    this.shots.remove(shotModel);
    notifyDataSetChanged();
  }

  public void setShots(List<ShotModel> shots) {
    this.shots = shots;
  }

  public void addShotsAbove(List<ShotModel> shotModels) {
    List<ShotModel> newShotList = new ArrayList<>(shotModels);
    newShotList.addAll(this.shots);
    this.shots = newShotList;
  }

  public void onPinnedShot(ShotModel shotModel) {
    List<ShotModel> shotModels = new ArrayList<>();
    for (ShotModel shot : shots) {
      if (shotModel.getIdShot().equals(shot.getIdShot())) {
        shotModels.add(shotModel);
      } else {
        shotModels.add(shot);
      }
    }
    this.shots = shotModels;
  }

  public ShotModel getItem(int position) {
    return shots.get(position);
  }

  public ShotModel getLastShot() {
    Integer shotsNumber = shots.size();
    if (shotsNumber > 0) {
      return shots.get(shots.size() - 1);
    } else {
      return shots.get(0);
    }
  }
}
