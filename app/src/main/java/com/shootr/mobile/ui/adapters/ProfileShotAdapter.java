package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.holders.ProfileShotViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnReshootClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;
import java.util.ArrayList;
import java.util.List;

public class ProfileShotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private final ImageLoader imageLoader;
  private final OnAvatarClickListener avatarClickListener;
  private final OnVideoClickListener videoClickListener;
  private final OnNiceShotListener onNiceShotListener;
  private final AndroidTimeUtils timeUtils;
  private final ShotClickListener shotClickListener;
  private final OnShotLongClick onShotLongClick;
  private final OnUrlClickListener onShotUrlClickListener;
  private final OnReshootClickListener onReshootClickListener;
  private final NumberFormatUtil numberFormatUtil;

  private List<ShotModel> shots;

  public ProfileShotAdapter(ImageLoader imageLoader, OnAvatarClickListener avatarClickListener,
      OnVideoClickListener videoClickListener, OnNiceShotListener onNiceShotListener,
      AndroidTimeUtils timeUtils, ShotClickListener shotClickListener,
      OnShotLongClick onShotLongClick, OnUrlClickListener onShotUrlClickListener,
      OnReshootClickListener onReshootClickListener, NumberFormatUtil numberFormatUtil) {
    this.imageLoader = imageLoader;
    this.avatarClickListener = avatarClickListener;
    this.videoClickListener = videoClickListener;
    this.onNiceShotListener = onNiceShotListener;
    this.timeUtils = timeUtils;
    this.shotClickListener = shotClickListener;
    this.onShotLongClick = onShotLongClick;
    this.onShotUrlClickListener = onShotUrlClickListener;
    this.onReshootClickListener = onReshootClickListener;
    this.numberFormatUtil = numberFormatUtil;
    shots = new ArrayList<>();
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_swipeable_profile_shot, parent, false);
    return new ProfileShotViewHolder(v, avatarClickListener, videoClickListener, onNiceShotListener,
        timeUtils, imageLoader, numberFormatUtil);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    ((ProfileShotViewHolder) holder).render(shots.get(position), shotClickListener,
        onShotLongClick, onReshootClickListener, onShotUrlClickListener);
  }

  @Override public int getItemCount() {
    return shots.size();
  }

  public void setShots(List<ShotModel> shots) {
    this.shots = shots;
  }

  public void addShotsBelow(List<ShotModel> newShots) {
    this.shots.addAll(newShots);
    notifyDataSetChanged();
  }

  public void removeShot(ShotModel shotModel) {
    this.shots.remove(shotModel);
    notifyDataSetChanged();
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
