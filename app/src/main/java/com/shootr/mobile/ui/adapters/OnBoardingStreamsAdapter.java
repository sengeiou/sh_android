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
import com.shootr.mobile.ui.adapters.holders.OnBoardingStreamViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnBoardingFavoriteClickListener;
import com.shootr.mobile.ui.model.OnBoardingModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import java.util.List;

public class OnBoardingStreamsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private static final int TYPE_STREAM = 0;
  private static final int TYPE_USER = 1;
  private static final int UNKNOWN = -1;

  private List<OnBoardingModel> onBoardingStreamModelList;
  private final OnBoardingFavoriteClickListener onFavoriteClickListener;
  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;

  private int lastAnimatedPosition = -1;
  private int screenHeight;

  public OnBoardingStreamsAdapter(OnBoardingFavoriteClickListener onFavoriteClickListener,
      ImageLoader imageLoader, InitialsLoader initialsLoader) {
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    this.onFavoriteClickListener = onFavoriteClickListener;
  }

  public void setOnBoardingStreamModelList(List<OnBoardingModel> onBoardingStreamModelList) {
    this.onBoardingStreamModelList = onBoardingStreamModelList;
  }

  @Override public int getItemViewType(int position) {
    if (onBoardingStreamModelList.get(position).getStreamModel() != null) {
      return TYPE_STREAM;
    } else if (onBoardingStreamModelList.get(position).getUserModel() != null) {
      return TYPE_USER;
    } else {
      return UNKNOWN;
    }
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.onboard_stream_card, parent, false);
    if (viewType == TYPE_STREAM) {
      return new OnBoardingStreamViewHolder(view, onFavoriteClickListener, imageLoader,
          initialsLoader);
    } else if (viewType == TYPE_USER) {
      return null;
    } else {
      return null;
    }
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    runEnterAnimation(holder.itemView, position);
    if (holder.getItemViewType() == TYPE_STREAM) {
      ((OnBoardingStreamViewHolder) holder).render(onBoardingStreamModelList.get(position));
    } else if (holder.getItemViewType() == TYPE_USER) {
      //TODO
    }
  }

  @Override public int getItemCount() {
    return onBoardingStreamModelList != null ? onBoardingStreamModelList.size() : 0;
  }

  private void runEnterAnimation(View view, int position) {
    if (position >= getItemCount()) {
      return;
    }

    if (position > lastAnimatedPosition) {
      lastAnimatedPosition = position;
      view.setTranslationY(getScreenHeight(view.getContext()));
      view.animate()
          .translationY(0)
          .setInterpolator(new DecelerateInterpolator(3.f))
          .setDuration(1000)
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

  public void updateFavorite(OnBoardingModel onBoardingStreamModel) {
    int position = onBoardingStreamModelList.indexOf(onBoardingStreamModel);
    onBoardingStreamModelList.get(position).setFavorite(!onBoardingStreamModel.isFavorite());
  }
}
