package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.shootr.mobile.R;

public class ConnectControllerBehavior extends CoordinatorLayout.Behavior<LinearLayout> {

  private int textIndicatorHeight = 48;
  private Context context;

  public ConnectControllerBehavior() {
  }

  public ConnectControllerBehavior(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
  }

  @Override
  public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout child, View dependency) {
    return dependency.getId() == R.id.bottomBar;
  }

  @Override
  public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout linearLayout, View dependency) {
    if (dependency.getId() == R.id.bottomBar) {
      linearLayout.setTranslationY(-dependency.getHeight() + dependency.getTranslationY());
    }
    return true;
  }
}

