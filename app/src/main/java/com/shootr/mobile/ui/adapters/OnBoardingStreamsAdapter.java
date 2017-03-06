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
import com.shootr.mobile.ui.model.OnBoardingStreamModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import java.util.List;

public class OnBoardingStreamsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private List<OnBoardingStreamModel> onBoardingStreamModelList;
  private final OnBoardingFavoriteClickListener onFavoriteClickListener;
  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;

  private int lastAnimatedPosition = -1;
  private int screenHeight;

  public OnBoardingStreamsAdapter(
      OnBoardingFavoriteClickListener onFavoriteClickListener, ImageLoader imageLoader,
      InitialsLoader initialsLoader) {
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    this.onFavoriteClickListener = onFavoriteClickListener;
  }

  public void setOnBoardingStreamModelList(List<OnBoardingStreamModel> onBoardingStreamModelList) {
    this.onBoardingStreamModelList = onBoardingStreamModelList;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.onboard_stream_card, parent, false);
    return new OnBoardingStreamViewHolder(view, onFavoriteClickListener,
        imageLoader, initialsLoader);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    runEnterAnimation(holder.itemView, position);
    ((OnBoardingStreamViewHolder) holder).render(onBoardingStreamModelList.get(position));
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
}
