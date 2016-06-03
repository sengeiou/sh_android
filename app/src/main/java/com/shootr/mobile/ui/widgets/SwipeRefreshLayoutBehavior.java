package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import com.shootr.mobile.R;

public class SwipeRefreshLayoutBehavior extends CoordinatorLayout.Behavior<SwipeRefreshLayout> {

  public SwipeRefreshLayoutBehavior(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
  }

  @Override public boolean layoutDependsOn(CoordinatorLayout parent, SwipeRefreshLayout child,
      View dependency) {
    return dependency.getId() == R.id.timeline_new_shots_indicator;
  }

  @Override
  public boolean onDependentViewChanged(CoordinatorLayout parent, SwipeRefreshLayout child,
      View dependency) {
    float translationY = Math.min(0, -dependency.getTranslationY() - dependency.getHeight());
    child.setTranslationY(-translationY);
    return true;
  }
}
