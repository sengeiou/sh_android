package com.shootr.mobile.ui.widgets;

import android.animation.Animator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;

public class QuickReturnHeaderBehavior extends CoordinatorLayout.Behavior<View> {

  private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
  private static final int ANIMATION_DURATION = 200;

  private boolean isShowing;
  private boolean isHiding;

  public QuickReturnHeaderBehavior(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child,
      View directTargetChild, View target, int nestedScrollAxes) {
    return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
  }

  @Override
  public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target,
      int horitzontalScroll, int verticalScroll, int[] consumed) {
    if (verticalScroll > 12) {
      hide(child);
    } else {
      show(child);
    }
  }

  private void hide(final View view) {
    isHiding = true;
    ViewPropertyAnimator animator = view.animate()
        .translationY(-view.getHeight())
        .setInterpolator(INTERPOLATOR)
        .setDuration(ANIMATION_DURATION);

    animator.setListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animator) {
        /* no-op */
      }

      @Override public void onAnimationEnd(Animator animator) {
        isHiding = false;
      }

      @Override public void onAnimationCancel(Animator animator) {
        isHiding = false;
        if (!isShowing) {
          show(view);
        }
      }

      @Override public void onAnimationRepeat(Animator animator) {
        /* no-op */
      }
    });

    animator.start();
  }

  private void show(final View view) {
    isShowing = true;
    ViewPropertyAnimator animator =
        view.animate().translationY(0).setInterpolator(INTERPOLATOR).setDuration(200);

    animator.setListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animator) {
        view.setVisibility(View.VISIBLE);
      }

      @Override public void onAnimationEnd(Animator animator) {
        isShowing = false;
      }

      @Override public void onAnimationCancel(Animator animator) {
        isShowing = false;
        if (!isHiding) {
          hide(view);
        }
      }

      @Override public void onAnimationRepeat(Animator animator) {
        /* no-op */
      }
    });

    animator.start();
  }
}
