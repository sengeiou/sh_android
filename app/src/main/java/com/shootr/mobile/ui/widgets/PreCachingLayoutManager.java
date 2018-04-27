package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

public class PreCachingLayoutManager extends LinearLayoutManager {
  private static final int DEFAULT_EXTRA_LAYOUT_SPACE = 2000;
  private static final float MILLISECONDS_PER_INCH = 90f;

  private Context context;

  public PreCachingLayoutManager(Context context) {
    super(context);
    this.context = context;
  }

  @Override
  protected int getExtraLayoutSpace(RecyclerView.State state) {
    return DEFAULT_EXTRA_LAYOUT_SPACE;
  }

  @Override public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
      final int position) {

    LinearSmoothScroller smoothScroller = new LinearSmoothScroller(context) {
      @Override public PointF computeScrollVectorForPosition(int targetPosition) {
        return PreCachingLayoutManager.this.computeScrollVectorForPosition(targetPosition);
      }

      @Override protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
      }
    };

    smoothScroller.setTargetPosition(position);
    startSmoothScroll(smoothScroller);
  }
}

