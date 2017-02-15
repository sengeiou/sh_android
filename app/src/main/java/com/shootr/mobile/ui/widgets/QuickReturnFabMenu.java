package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import com.github.clans.fab.FloatingActionMenu;


public class QuickReturnFabMenu extends CoordinatorLayout.Behavior<View> {

  public QuickReturnFabMenu(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final View child,
      final View directTargetChild, final View target, final int nestedScrollAxes) {
    return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
  }

  @Override
  public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {

    if (dy > 0) {
      ((FloatingActionMenu) child).hideMenu(true);
    } else {
      ((FloatingActionMenu) child).showMenu(true);
    }
  }
}

