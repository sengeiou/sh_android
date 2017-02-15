package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionMenu;
import java.util.List;

/**
 * Created by miniserver on 13/2/17.
 */

public class QuickReturnFabMenu extends CoordinatorLayout.Behavior<View> {

  private int mDySinceDirectionChange;
  private int mScrollOffset = 4;
  private int lastVerticalScroll;

  public QuickReturnFabMenu(Context context, AttributeSet attrs) {
    super(context, attrs);
  }



  @Override
  public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final View child,
      final View directTargetChild, final View target, final int nestedScrollAxes) {
    // Ensure we react to vertical scrolling
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

