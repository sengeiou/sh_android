package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.shootr.mobile.R;

public class NewShotsNotificatorBehavior extends CoordinatorLayout.Behavior<RelativeLayout> {

  private int textIndicatorHeight = 48;
  private Context context;

  public NewShotsNotificatorBehavior() {
  }

  public NewShotsNotificatorBehavior(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
  }

  @Override
  public boolean layoutDependsOn(CoordinatorLayout parent, RelativeLayout linearLayout, View dependency) {
    return dependency.getId() == R.id.timeline_indicator;
  }

  @Override
  public boolean onDependentViewChanged(CoordinatorLayout parent, RelativeLayout linearLayout, View dependency) {
    if (dependency.getId() == R.id.timeline_indicator) {
      linearLayout.setTranslationY(dependency.getHeight() + dependency.getTranslationY());
    }
    return true;
  }
}

