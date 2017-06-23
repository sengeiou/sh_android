package com.shootr.mobile.ui.widgets;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class BottomOffsetDecoration extends RecyclerView.ItemDecoration {

  private int bottomOffset;

  public BottomOffsetDecoration(int bottomOffset) {
    this.bottomOffset = bottomOffset;
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    super.getItemOffsets(outRect, view, parent, state);
    int dataSize = state.getItemCount();
    int position = parent.getChildPosition(view);
    if (parent.getLayoutManager() instanceof GridLayoutManager) {
      GridLayoutManager grid = ((GridLayoutManager) parent.getLayoutManager());
      if ((dataSize - position) == 1) {
        outRect.set(0, 0, 0, bottomOffset);
      } else {
        outRect.set(0, 0, 0, 0);
      }
    } else {
      if (dataSize > 0 && position == dataSize - 1) {
        outRect.set(0, 0, 0, bottomOffset);
      } else {
        outRect.set(0, 0, 0, 0);
      }
    }
  }
}
