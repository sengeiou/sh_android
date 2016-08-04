package com.shootr.mobile.ui.adapters;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.holders.LoadingViewHolder;
import com.shootr.mobile.ui.adapters.holders.ShotTimelineViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageLongClickListener;
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

  private static final int ANIMATED_ITEMS_COUNT = 10;
  private final int VIEW_TYPE_ITEM = 0;
  private final ImageLoader imageLoader;
  private final OnAvatarClickListener avatarClickListener;
  private final OnVideoClickListener videoClickListener;
  private final OnNiceShotListener onNiceShotListener;
  private final OnUsernameClickListener onUsernameClickListener;
  private final OnReplyShotListener onReplyShotListener;
  private final AndroidTimeUtils timeUtils;
  private final ShotTextSpannableBuilder shotTextSpannableBuilder;
  private final ShotClickListener shotClickListener;
  private final OnShotLongClick onShotLongClick;
  private final OnImageLongClickListener onLongClickListener;
  private final View.OnTouchListener onTouchListener;
  private final OnImageClickListener onImageClickListener;
  private final Context context;

  private List<ShotModel> shots;

  private int screenHeight;
  private int lastAnimatedPosition = -1;

  public ShotsTimelineAdapter(Context context, ImageLoader imageLoader, AndroidTimeUtils timeUtils,
      OnAvatarClickListener avatarClickListener, OnVideoClickListener videoClickListener,
      OnNiceShotListener onNiceShotListener, OnUsernameClickListener onUsernameClickListener,
      OnReplyShotListener onReplyShotListener, ShotClickListener shotClickListener, OnShotLongClick onShotLongClick,
      OnImageLongClickListener onLongClickListener, View.OnTouchListener onTouchListener,
      OnImageClickListener onImageClickListener) {
    this.imageLoader = imageLoader;
    this.avatarClickListener = avatarClickListener;
    this.videoClickListener = videoClickListener;
    this.onNiceShotListener = onNiceShotListener;
    this.onUsernameClickListener = onUsernameClickListener;
    this.timeUtils = timeUtils;
    this.onReplyShotListener = onReplyShotListener;
    this.context = context;
    this.shots = new ArrayList<>(0);
    this.shotTextSpannableBuilder = new ShotTextSpannableBuilder();
    this.shotClickListener = shotClickListener;
    this.onShotLongClick = onShotLongClick;
    this.onLongClickListener = onLongClickListener;
    this.onTouchListener = onTouchListener;
    this.onImageClickListener = onImageClickListener;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == VIEW_TYPE_ITEM) {
      View v = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_shot_timeline, parent, false);
      return new ShotTimelineViewHolder(v, avatarClickListener, videoClickListener,
          onNiceShotListener, onReplyShotListener, onUsernameClickListener,
          timeUtils, imageLoader, shotTextSpannableBuilder);
    } else {
      View v =
          LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_shot, parent, false);
      return new LoadingViewHolder(v);
    }
  }

  @Override public int getItemViewType(int position) {
    int VIEW_TYPE_LOADING = 1;
    return shots.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    runEnterAnimation(holder.itemView, position);
    if (holder instanceof ShotTimelineViewHolder) {
      ((ShotTimelineViewHolder) holder).render(shots.get(position), shotClickListener,
          onShotLongClick, onLongClickListener, onTouchListener, onImageClickListener);
    } else if (holder instanceof LoadingViewHolder) {
      ((LoadingViewHolder) holder).render();
    }
  }

  private void runEnterAnimation(View view, int position) {
    if (position >= ANIMATED_ITEMS_COUNT) {
      return;
    }

    if (position > lastAnimatedPosition) {
      lastAnimatedPosition = position;
      view.setTranslationY(getScreenHeight(context));
      view.animate()
          .translationY(0)
          .setInterpolator(new DecelerateInterpolator(3.f))
          .setDuration(600)
          .start();
    }
  }

  private int getScreenHeight(Context c) {
    if (screenHeight == 0) {
      WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
      Display display = wm.getDefaultDisplay();
      Point size = new Point();
      display.getSize(size);
      screenHeight = size.y;
    }
    return screenHeight;
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
