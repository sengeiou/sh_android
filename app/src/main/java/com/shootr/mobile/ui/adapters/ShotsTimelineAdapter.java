package com.shootr.mobile.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.holders.HighLightedShotViewHolder;
import com.shootr.mobile.ui.adapters.holders.ShotTimelineViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnHideHighlightShot;
import com.shootr.mobile.ui.adapters.listeners.OnImageClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageLongClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnReshootClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.adapters.recyclerview.SubheaderRecyclerViewAdapter;
import com.shootr.mobile.ui.model.HighlightedShotModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShotTextSpannableBuilder;
import java.util.ArrayList;
import java.util.List;

public class ShotsTimelineAdapter
    extends SubheaderRecyclerViewAdapter<RecyclerView.ViewHolder, ShotModel, ShotModel> {

  private static final int HEADER_POSITION = 0;

  private static final int ITEM_POSITION_WITH_HEADER = 1;
  private static final int ITEM_POSITION_WITHOUT_HEADER = 0;

  private final ImageLoader imageLoader;
  private final OnAvatarClickListener avatarClickListener;
  private final OnVideoClickListener videoClickListener;
  private final OnNiceShotListener onNiceShotListener;
  private final OnUsernameClickListener onUsernameClickListener;
  private final AndroidTimeUtils timeUtils;
  private final ShotTextSpannableBuilder shotTextSpannableBuilder;
  private final ShotClickListener shotClickListener;
  private final OnShotLongClick onShotLongClick;
  private final OnImageLongClickListener onLongClickListener;
  private final View.OnTouchListener onTouchListener;
  private final OnImageClickListener onImageClickListener;
  private final OnHideHighlightShot onHideHighlightClickListener;
  private final OnUrlClickListener onUrlClickListener;
  private final OnUrlClickListener onShotUrlClickListener;
  private final OnReshootClickListener onReshootClickListener;
  private final NumberFormatUtil numberFormatUtil;

  private List<ShotModel> shots;
  private HighlightedShotModel highlightedShotModel;
  private Boolean isAdmin;

  public ShotsTimelineAdapter(ImageLoader imageLoader, AndroidTimeUtils timeUtils,
      OnAvatarClickListener avatarClickListener, OnVideoClickListener videoClickListener,
      OnNiceShotListener onNiceShotListener, OnUsernameClickListener onUsernameClickListener,
      ShotClickListener shotClickListener, OnShotLongClick onShotLongClick,
      OnImageLongClickListener onLongClickListener, View.OnTouchListener onTouchListener,
      OnImageClickListener onImageClickListener, OnUrlClickListener onUrlClickListener,
      OnUrlClickListener onShotUrlClickListener,
      OnHideHighlightShot onHideHighlightClickListener,
      OnReshootClickListener onReshootClickListener, NumberFormatUtil numberFormatUtil,
      Boolean isAdmin) {
    this.imageLoader = imageLoader;
    this.avatarClickListener = avatarClickListener;
    this.videoClickListener = videoClickListener;
    this.onNiceShotListener = onNiceShotListener;
    this.onUsernameClickListener = onUsernameClickListener;
    this.timeUtils = timeUtils;
    this.onUrlClickListener = onUrlClickListener;
    this.onShotUrlClickListener = onShotUrlClickListener;
    this.onHideHighlightClickListener = onHideHighlightClickListener;
    this.onReshootClickListener = onReshootClickListener;
    this.numberFormatUtil = numberFormatUtil;
    this.shots = new ArrayList<>(0);
    this.shotTextSpannableBuilder = new ShotTextSpannableBuilder();
    this.shotClickListener = shotClickListener;
    this.onShotLongClick = onShotLongClick;
    this.onLongClickListener = onLongClickListener;
    this.onTouchListener = onTouchListener;
    this.onImageClickListener = onImageClickListener;
    this.isAdmin = isAdmin;
  }

  @Override
  protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
    View v =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.highlighted_shot, parent, false);
    return new HighLightedShotViewHolder(v, avatarClickListener, videoClickListener,
        onNiceShotListener, onHideHighlightClickListener,
        onUsernameClickListener, timeUtils, imageLoader,
        shotTextSpannableBuilder, numberFormatUtil);
  }

  @Override
  protected RecyclerView.ViewHolder onCreateSubheaderViewHolder(ViewGroup parent, int viewType) {
    return getShotViewHolder(parent);
  }

  @NonNull RecyclerView.ViewHolder getShotViewHolder(ViewGroup parent) {
    View v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_swipeable_shot_timeline, parent, false);
    return new ShotTimelineViewHolder(v, avatarClickListener, videoClickListener,
        onNiceShotListener, onUsernameClickListener, timeUtils, imageLoader,
        numberFormatUtil, shotTextSpannableBuilder);
  }

  @Override
  protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
    return getShotViewHolder(parent);
  }

  @Override protected void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
    ((HighLightedShotViewHolder) holder).renderHighLight(highlightedShotModel, shots.get(position), shotClickListener,
        onShotLongClick, onLongClickListener, onTouchListener, onImageClickListener, onUrlClickListener, isAdmin);
  }

  @Override protected void onBindSubheaderViewHolder(RecyclerView.ViewHolder holder, int position) {
    renderShotViewHolder((ShotTimelineViewHolder) holder, position);
  }

  private void renderShotViewHolder(ShotTimelineViewHolder holder, int position) {
    holder.render(shots.get(position), shotClickListener, onShotLongClick, onLongClickListener,
        onTouchListener, onImageClickListener, onReshootClickListener, onShotUrlClickListener);
  }

  @Override protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
    renderShotViewHolder((ShotTimelineViewHolder) holder, position);
  }

  public void setHighlightedShot(HighlightedShotModel highlightedShot) {
    if (!hasHeader()) {
      putNewHeader(highlightedShot);
    } else {
      removeCurrentHeader();
      putNewHeader(highlightedShot);
    }
    this.highlightedShotModel = highlightedShot;
    notifyDataSetChanged();
  }

  private void removeCurrentHeader() {
    shots.remove(HEADER_POSITION);
    notifyItemRemoved(HEADER_POSITION);
  }

  private void putNewHeader(HighlightedShotModel highlightedShot) {
    shots.add(HEADER_POSITION, highlightedShot.getShotModel());
    this.setHeader(highlightedShot.getShotModel());
  }

  public void removeHighlightShot() {
    if (hasHeader()) {
      removeCurrentHeader();
      setHeader(null);
    }
  }

  @Override public int getItemCount() {
    return shots.size();
  }

  public void addShotsBelow(List<ShotModel> newShots) {
    if (hasHeader()) {
      shots.remove(0);
    }
    this.shots.addAll(newShots);
    insertExistingHeader(shots);
    notifyDataSetChanged();
  }

  public void removeShot(ShotModel shotModel) {
    this.shots.remove(shotModel);
    notifyDataSetChanged();
  }

  public void setShots(List<ShotModel> shots) {
    this.shots = shots;
    insertExistingHeader(shots);
  }

  private void insertExistingHeader(List<ShotModel> shots) {
    if (hasHeader()) {
      shots.add(HEADER_POSITION, getHeader());
    }
  }

  public void addShotsAbove(List<ShotModel> shotModels) {
    List<ShotModel> newShotList = new ArrayList<>(shotModels);
    if (hasHeader()) {
      shots.remove(0);
    }
    newShotList.addAll(this.shots);
    this.shots = newShotList;
    insertExistingHeader(shots);
  }

  public void addShots(List<ShotModel> shotModels) {
    List<ShotModel> newShotList = new ArrayList<>(shotModels);
    if (hasHeader()) {
      insertNewShots(newShotList, ITEM_POSITION_WITH_HEADER, newShotList.size());
    } else {
      insertNewShots(newShotList, ITEM_POSITION_WITHOUT_HEADER, newShotList.size());
    }
  }

  private void insertNewShots(List<ShotModel> newShotList, int position, int size) {
    shots.addAll(position, newShotList);
    notifyItemRangeInserted(position, size);
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

  public void setHighlightShotBackground(Boolean isAdmin) {
    this.isAdmin = isAdmin;
    notifyDataSetChanged();
  }

  public void updateHighligthShotInfo(HighlightedShotModel highlightedShotModel) {
    if (!getHeader().equals(highlightedShotModel.getShotModel())) {
      shots.remove(HEADER_POSITION);
      shots.add(HEADER_POSITION, highlightedShotModel.getShotModel());
      this.highlightedShotModel = highlightedShotModel;
      setHeader(highlightedShotModel.getShotModel());
      notifyDataSetChanged();
    }
  }
}
