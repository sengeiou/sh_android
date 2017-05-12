package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.holders.DiscoverStreamViewHolder;
import com.shootr.mobile.ui.adapters.holders.ShotTimelineViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnDiscoverTimelineFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnDiscoveredStreamClickListener;
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
import com.shootr.mobile.ui.model.DiscoverStreamModel;
import com.shootr.mobile.ui.model.DiscoverTimelineModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShotTextSpannableBuilder;
import java.util.ArrayList;

public class DiscoverTimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private static final int TYPE_STREAM = 0;
  private static final int TYPE_SHOT = 1;
  private static final int UNKNOW = -1;

  private final ImageLoader imageLoader;
  private final OnDiscoveredStreamClickListener onDiscoveredStreamClickListener;
  private final OnDiscoverTimelineFavoriteClickListener onFavoriteClickListener;
  private final OnAvatarClickListener onAvatarClickListener;
  private final OnVideoClickListener videoClickListener;
  private final OnNiceShotListener onNiceShotListener;
  private final OnOpenShotMenuListener onOpenShotMenuListener;
  private final OnUsernameClickListener onUsernameClickListener;
  private final AndroidTimeUtils timeUtils;
  private final NumberFormatUtil numberFormatUtil;
  private final ShotTextSpannableBuilder shotTextSpannableBuilder;
  private final ShotClickListener shotClickListener;
  private final OnShotLongClick onShotLongClick;
  private final OnImageLongClickListener onLongClickListener;
  private final View.OnTouchListener onTouchListener;
  private final OnImageClickListener onImageClickListener;
  private final OnUrlClickListener onShotUrlClickListener;
  private final OnReshootClickListener onReshootClickListener;
  private final InitialsLoader initialsLoader;

  private ArrayList<Object> items;

  public DiscoverTimelineAdapter(ImageLoader imageLoader, InitialsLoader initialsLoader,
      OnDiscoveredStreamClickListener onDiscoveredStreamClickListener,
      OnDiscoverTimelineFavoriteClickListener onFavoriteClickListener,
      OnAvatarClickListener onAvatarClickListener, OnVideoClickListener videoClickListener,
      OnNiceShotListener onNiceShotListener, OnOpenShotMenuListener onOpenShotMenuListener,
      OnUsernameClickListener onUsernameClickListener, AndroidTimeUtils timeUtils,
      NumberFormatUtil numberFormatUtil, ShotTextSpannableBuilder shotTextSpannableBuilder,
      ShotClickListener shotClickListener, OnShotLongClick onShotLongClick,
      OnImageLongClickListener onLongClickListener, View.OnTouchListener onTouchListener,
      OnImageClickListener onImageClickListener, OnUrlClickListener onShotUrlClickListener,
      OnReshootClickListener onReshootClickListener) {
    this.imageLoader = imageLoader;
    this.onDiscoveredStreamClickListener = onDiscoveredStreamClickListener;
    this.onFavoriteClickListener = onFavoriteClickListener;
    this.onAvatarClickListener = onAvatarClickListener;
    this.videoClickListener = videoClickListener;
    this.onNiceShotListener = onNiceShotListener;
    this.onOpenShotMenuListener = onOpenShotMenuListener;
    this.onUsernameClickListener = onUsernameClickListener;
    this.timeUtils = timeUtils;
    this.numberFormatUtil = numberFormatUtil;
    this.shotTextSpannableBuilder = shotTextSpannableBuilder;
    this.shotClickListener = shotClickListener;
    this.onShotLongClick = onShotLongClick;
    this.onLongClickListener = onLongClickListener;
    this.onTouchListener = onTouchListener;
    this.onImageClickListener = onImageClickListener;
    this.onShotUrlClickListener = onShotUrlClickListener;
    this.onReshootClickListener = onReshootClickListener;
    this.initialsLoader = initialsLoader;
  }

  @Override public int getItemViewType(int position) {
    if (items.get(position) instanceof StreamModel) {
      return  TYPE_STREAM;
    } else if (items.get(position) instanceof ShotModel) {
      return TYPE_SHOT;
    } else {
      return UNKNOW;
    }
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    RecyclerView.ViewHolder viewHolder;
    View view;

    if (viewType == TYPE_STREAM) {
      view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_discover_stream, parent, false);
      viewHolder = new DiscoverStreamViewHolder(view, onDiscoveredStreamClickListener,
          onFavoriteClickListener, imageLoader, initialsLoader);
    } else if (viewType == TYPE_SHOT) {
      view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_swipeable_shot_timeline, parent, false);
      viewHolder = new ShotTimelineViewHolder(view, onAvatarClickListener, videoClickListener,
          onNiceShotListener, onOpenShotMenuListener, onUsernameClickListener, timeUtils,
          imageLoader, numberFormatUtil, shotTextSpannableBuilder);
    } else {
      return null;
    }

    return viewHolder;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder != null) {
      if (holder.getItemViewType() == TYPE_STREAM) {
        ((DiscoverStreamViewHolder) holder).render((StreamModel) items.get(position));
      } else if (holder.getItemViewType() == TYPE_SHOT) {
        ((ShotTimelineViewHolder) holder).render((ShotModel) items.get(position), shotClickListener,
            onShotLongClick, onLongClickListener, onTouchListener, onImageClickListener,
            onReshootClickListener, onShotUrlClickListener, onOpenShotMenuListener);
      }
    }
  }

  @Override public int getItemCount() {
    return items != null ? items.size() : 0;
  }

  public void setItems(DiscoverTimelineModel discoverTimelineModel) {
    items = new ArrayList<>();

    for (DiscoverStreamModel discoverStreamModel : discoverTimelineModel.getDiscoverStreamModels()) {
      items.add(discoverStreamModel.getStreamModel());
      for (ShotModel shotModel : discoverStreamModel.getShotModels()) {
        items.add(shotModel);
      }
    }
  }

  public void markNice(ShotModel shotModel) {
    int index = items.indexOf(shotModel);
    ShotModel shot = (ShotModel) items.get(index);
    int niceCount = shot.getNiceCount() + 1;
    shot.setNiced(true);
    shot.setNiceCount(niceCount);
    notifyItemChanged(index);
  }

  public void unmarkNice(String idShot) {
    int index = 0;
    for (Object object : items) {
      if (object instanceof ShotModel) {
        ShotModel shot = (ShotModel) object;
        if (shot.getIdShot().equals(idShot)) {
          int niceCount = shot.getNiceCount() - 1;
          shot.setNiceCount(niceCount);
          shot.setNiced(false);
          notifyItemChanged(index);
        }
        index++;
      }
    }
  }

  public void addFavorite(StreamModel streamModel) {
    int index = items.indexOf(streamModel);
    StreamModel stream = (StreamModel) items.get(index);
    stream.setFavorite(true);
    notifyItemChanged(index);
  }

  public void removeFavorite(StreamModel streamModel) {
    int index = items.indexOf(streamModel);
    StreamModel stream = (StreamModel) items.get(index);
    stream.setFavorite(false);
    notifyItemChanged(index);
  }

  public void reshoot(ShotModel shotModel, boolean mark) {
    int index = items.indexOf(shotModel);
    ShotModel shot = (ShotModel) items.get(index);
    shot.setReshooted(mark);
    notifyItemChanged(index);
  }

}
