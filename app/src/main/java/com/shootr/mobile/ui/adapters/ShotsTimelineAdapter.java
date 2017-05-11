package com.shootr.mobile.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.model.shot.ShotType;
import com.shootr.mobile.ui.adapters.holders.HighLightedShotViewHolder;
import com.shootr.mobile.ui.adapters.holders.HighlightedPromotedShotViewHolder;
import com.shootr.mobile.ui.adapters.holders.PromotedShotViewHolder;
import com.shootr.mobile.ui.adapters.holders.ShotTimelineViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnCtaClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnHideHighlightShot;
import com.shootr.mobile.ui.adapters.listeners.OnImageClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageLongClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnOpenShotMenuListener;
import com.shootr.mobile.ui.adapters.listeners.OnReshootClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.adapters.recyclerview.SubheaderShotRecyclerViewAdapter;
import com.shootr.mobile.ui.model.HighlightedShotModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShotTextSpannableBuilder;
import java.util.ArrayList;
import java.util.List;

public class ShotsTimelineAdapter
    extends SubheaderShotRecyclerViewAdapter<RecyclerView.ViewHolder, ShotModel, ShotModel> {

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
  private final OnOpenShotMenuListener onOpenShotMenuListener;
  private final OnImageLongClickListener onLongClickListener;
  private final View.OnTouchListener onTouchListener;
  private final OnImageClickListener onImageClickListener;
  private final OnHideHighlightShot onHideHighlightClickListener;
  private final OnUrlClickListener onUrlClickListener;
  private final OnUrlClickListener onShotUrlClickListener;
  private final OnReshootClickListener onReshootClickListener;
  private final OnCtaClickListener onCtaClickListener;
  private final NumberFormatUtil numberFormatUtil;

  private List<ShotModel> shots;
  private HighlightedShotModel highlightedShotModel;
  private Boolean isAdmin;

  public ShotsTimelineAdapter(ImageLoader imageLoader, AndroidTimeUtils timeUtils,
      OnAvatarClickListener avatarClickListener, OnVideoClickListener videoClickListener,
      OnNiceShotListener onNiceShotListener, OnUsernameClickListener onUsernameClickListener,
      ShotClickListener shotClickListener, OnShotLongClick onShotLongClick,
      OnOpenShotMenuListener onOpenShotMenuListener, OnImageLongClickListener onLongClickListener,
      View.OnTouchListener onTouchListener, OnImageClickListener onImageClickListener,
      OnUrlClickListener onUrlClickListener, OnUrlClickListener onShotUrlClickListener,
      OnHideHighlightShot onHideHighlightClickListener,
      OnReshootClickListener onReshootClickListener, OnCtaClickListener onCtaClickListener,
      NumberFormatUtil numberFormatUtil, Boolean isAdmin) {
    this.imageLoader = imageLoader;
    this.avatarClickListener = avatarClickListener;
    this.videoClickListener = videoClickListener;
    this.onNiceShotListener = onNiceShotListener;
    this.onUsernameClickListener = onUsernameClickListener;
    this.timeUtils = timeUtils;
    this.onOpenShotMenuListener = onOpenShotMenuListener;
    this.onUrlClickListener = onUrlClickListener;
    this.onShotUrlClickListener = onShotUrlClickListener;
    this.onHideHighlightClickListener = onHideHighlightClickListener;
    this.onReshootClickListener = onReshootClickListener;
    this.onCtaClickListener = onCtaClickListener;
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

  @Override public int getItemViewType(int position) {
    int typeHeader = TYPE_ITEM_SHOT;
    ShotModel shotModel =
        shots != null && !shots.isEmpty() ? (ShotModel) shots.get(position) : null;
    if (isHeaderPosition(position)) {
      typeHeader = getHeaderType(shotModel);
    } else if (isSubheaderPosition(position)) {
      typeHeader = getSubheaderType(shotModel);
    } else {
      typeHeader = getItemType(shotModel);
    }
    return typeHeader;
  }

  private int getItemType(ShotModel shotModel) {
    int typeHeader = TYPE_ITEM_SHOT;
    if (shotModel != null) {
      if (shotModel.getType().equals(ShotType.CTACHECKIN) || shotModel.getType()
          .equals(ShotType.CTAGENERICLINK)) {
        typeHeader = TYPE_ITEM_CHECK_IN;
      } else {
        typeHeader = TYPE_ITEM_SHOT;
      }
    }
    return typeHeader;
  }

  private int getSubheaderType(ShotModel shotModel) {
    int typeHeader = TYPE_SUBHEADER_SHOT;
    if (shotModel != null) {
      if (shotModel.getType().equals(ShotType.CTACHECKIN) || shotModel.getType()
          .equals(ShotType.CTAGENERICLINK)) {
        typeHeader = TYPE_SUBHEADER_CHECK_IN;
      } else {
        typeHeader = TYPE_SUBHEADER_SHOT;
      }
    }
    return typeHeader;
  }

  private int getHeaderType(ShotModel shotModel) {
    int typeHeader = TYPE_HEADER_SHOT;
    if (shotModel != null) {
      if (shotModel.getType().equals(ShotType.CTACHECKIN) || shotModel.getType()
          .equals(ShotType.CTAGENERICLINK)) {
        typeHeader = TYPE_HEADER_CHECK_IN;
      } else {
        typeHeader = TYPE_HEADER_SHOT;
      }
    }
    return typeHeader;
  }

  @Override
  protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
    if (viewType == TYPE_HEADER_CHECK_IN) {
      View v = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.highlighted_shot_promoted, parent, false);
      return new HighlightedPromotedShotViewHolder(v, avatarClickListener, videoClickListener,
          onNiceShotListener, onOpenShotMenuListener, onHideHighlightClickListener,
          onUsernameClickListener, timeUtils, imageLoader, shotTextSpannableBuilder,
          numberFormatUtil);
    } else {
      View v = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.highlighted_shot, parent, false);
      return new HighLightedShotViewHolder(v, avatarClickListener, videoClickListener,
          onNiceShotListener, onOpenShotMenuListener, onHideHighlightClickListener,
          onUsernameClickListener, timeUtils, imageLoader, shotTextSpannableBuilder,
          numberFormatUtil);
    }
  }

  @Override
  protected RecyclerView.ViewHolder onCreateSubheaderViewHolder(ViewGroup parent, int viewType) {
    return getShotViewHolder(parent, viewType);
  }

  @NonNull RecyclerView.ViewHolder getShotViewHolder(ViewGroup parent, int viewType) {
    if (viewType == TYPE_SUBHEADER_CHECK_IN || viewType == TYPE_ITEM_CHECK_IN) {
      View v = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_swipeable_promoted_shot, parent, false);
      return new PromotedShotViewHolder(v, avatarClickListener, videoClickListener,
          onNiceShotListener, onOpenShotMenuListener, onUsernameClickListener, timeUtils,
          imageLoader, numberFormatUtil, shotTextSpannableBuilder);
    } else {
      View v = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_swipeable_shot_timeline, parent, false);
      return new ShotTimelineViewHolder(v, avatarClickListener, videoClickListener,
          onNiceShotListener, onOpenShotMenuListener, onUsernameClickListener, timeUtils,
          imageLoader, numberFormatUtil, shotTextSpannableBuilder);
    }
  }

  @Override
  protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
    return getShotViewHolder(parent, viewType);
  }

  @Override protected void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder.getItemViewType() == TYPE_HEADER_CHECK_IN) {
      ((HighlightedPromotedShotViewHolder) holder).render(highlightedShotModel, shots.get(position),
          shotClickListener, onShotLongClick, onLongClickListener, onTouchListener,
          onImageClickListener, onUrlClickListener, onOpenShotMenuListener, onReshootClickListener, isAdmin,
          onCtaClickListener);
    } else {
      ((HighLightedShotViewHolder) holder).renderHighLight(highlightedShotModel,
          shots.get(position), shotClickListener, onShotLongClick, onLongClickListener,
          onTouchListener, onImageClickListener, onUrlClickListener, onOpenShotMenuListener, onReshootClickListener,
          isAdmin);
    }
  }

  @Override protected void onBindSubheaderViewHolder(RecyclerView.ViewHolder holder, int position) {
    renderShotViewHolder((ShotTimelineViewHolder) holder, position);
  }

  private void renderShotViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder.getItemViewType() == TYPE_SUBHEADER_CHECK_IN
        || holder.getItemViewType() == TYPE_ITEM_CHECK_IN) {
      ((PromotedShotViewHolder) holder).render(shots.get(position), shotClickListener,
          onShotLongClick, onLongClickListener, onTouchListener, onImageClickListener,
          onReshootClickListener, onShotUrlClickListener, onOpenShotMenuListener,
          onCtaClickListener);
    } else {
      ((ShotTimelineViewHolder) holder).render(shots.get(position), shotClickListener,
          onShotLongClick, onLongClickListener, onTouchListener, onImageClickListener,
          onReshootClickListener, onShotUrlClickListener, onOpenShotMenuListener);
    }
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
    if (getHeader() != null) {
      if (!getHeader().equals(highlightedShotModel.getShotModel())) {
        shots.remove(HEADER_POSITION);
        shots.add(HEADER_POSITION, highlightedShotModel.getShotModel());
        this.highlightedShotModel = highlightedShotModel;
        setHeader(highlightedShotModel.getShotModel());
        notifyDataSetChanged();
      }
    }
  }

  public void markNice(ShotModel shotModel) {
    int index = 0;
    for (ShotModel shot : shots) {
      if (shot.getIdShot().equals(shotModel.getIdShot())) {
        int niceCount = shot.getNiceCount() + 1;
        shot.setNiceCount(niceCount);
        shot.setNiced(true);
        notifyItemChanged(index);
      }
      index++;
    }

  }

  public void unmarkNice(String idShot) {
    int index = 0;
    for (ShotModel shot : shots) {
      if (shot.getIdShot().equals(idShot)) {
        int niceCount = shot.getNiceCount() - 1;
        shot.setNiceCount(niceCount);
        shot.setNiced(false);
        notifyItemChanged(index);
      }
      index++;
    }
  }

  public void reshoot(String idShot, boolean mark) {
    int index = 0;
    for (ShotModel shot : shots) {
      if (shot.getIdShot().equals(idShot)) {
        shot.setReshooted(mark);
        notifyItemChanged(index);
      }
      index++;
    }
  }
}
