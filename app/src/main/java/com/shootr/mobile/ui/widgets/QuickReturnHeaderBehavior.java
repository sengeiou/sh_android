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

  public static final String NO_MESSAGE = "noMessage";
  public static final String MESSAGE = "message";

  private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

  private int verticalScrollInPixels;
  private boolean isShowing;
  private boolean isHiding;
  private String message;
  private Integer dependentViewId;
  private Integer animationDuration = 200;
  private Integer paddingDependentView = 144;

  private QuickReturnHeaderBehavior(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  private QuickReturnHeaderBehavior(Builder builder) {
    message = builder.message;
    dependentViewId = builder.dependentViewId;
    animationDuration = builder.animationDuration;
    paddingDependentView = builder.paddingDependentView;
  }

  @Override public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child,
      View directTargetChild, View target, int nestedScrollAxes) {
    return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
  }

  @Override
  public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target,
      int horitzontalScroll, int verticalScroll, int[] consumed) {
    View dependentView = coordinatorLayout.findViewById(dependentViewId);
    if (verticalScroll > 0 && verticalScrollInPixels < 0
        || verticalScroll < 0 && verticalScrollInPixels > 0) {
      child.animate().cancel();
      verticalScrollInPixels = 0;
    }

    verticalScrollInPixels += verticalScroll;

    if (verticalScrollInPixels > child.getHeight()
        && child.getVisibility() == View.VISIBLE
        && !isHiding) {
      hide(child, dependentView);
    } else if (verticalScrollInPixels < 0 && child.getVisibility() == View.GONE && !isShowing) {
      show(child, dependentView);
    }
  }

  private void hide(final View view, final View dependentView) {
    if (message.equals(MESSAGE)) {
      dependentView.setPadding(0, 0, 0, 0);
      isHiding = true;
      ViewPropertyAnimator animator = view.animate()
          .translationY(-view.getHeight())
          .setInterpolator(INTERPOLATOR)
          .setDuration(animationDuration);

      ViewPropertyAnimator listAnimator = dependentView.animate()
          .translationY(0)
          .setInterpolator(INTERPOLATOR)
          .setDuration(animationDuration);

      Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        @Override public void onAnimationStart(Animator animator) {
                /* no-op */
        }

        @Override public void onAnimationEnd(Animator animator) {
          isHiding = false;
          view.setVisibility(View.GONE);
        }

        @Override public void onAnimationCancel(Animator animator) {
          isHiding = false;
          if (!isShowing) {
            show(view, dependentView);
          }
        }

        @Override public void onAnimationRepeat(Animator animator) {
                /* no-op */
        }
      };

      animator.setListener(animatorListener);
      listAnimator.setListener(animatorListener);

      animator.start();
      listAnimator.start();
    }
  }

  private void show(final View view, final View dependentView) {
    if (message.equals(MESSAGE)) {
      dependentView.setPadding(0, 0, 0, 0);
      isShowing = true;
      ViewPropertyAnimator animator = view.animate()
          .translationY(0)
          .setInterpolator(INTERPOLATOR)
          .setDuration(animationDuration);
      ViewPropertyAnimator listAnimator = dependentView.animate()
          .translationY(paddingDependentView)
          .setInterpolator(INTERPOLATOR)
          .setDuration(animationDuration);

      Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        @Override public void onAnimationStart(Animator animator) {
          view.setVisibility(View.VISIBLE);
        }

        @Override public void onAnimationEnd(Animator animator) {
          isShowing = false;
        }

        @Override public void onAnimationCancel(Animator animator) {
          isShowing = false;
          if (!isHiding) {
            hide(view, dependentView);
          }
        }

        @Override public void onAnimationRepeat(Animator animator) {
                /* no-op */
        }
      };

      animator.setListener(animatorListener);
      listAnimator.setListener(animatorListener);

      animator.start();
      listAnimator.start();
    }
  }

  public static class Builder {
    private String message;
    private Integer dependentViewId;
    private Integer animationDuration;
    private Integer paddingDependentView;

    public Builder(String message, Integer dependentViewId, Integer paddingDependentView,
        Integer animationDuration) {
      this.message = message;
      this.dependentViewId = dependentViewId;
      this.animationDuration = animationDuration;
      this.paddingDependentView = paddingDependentView;
    }

    public QuickReturnHeaderBehavior build() {
      return new QuickReturnHeaderBehavior(this);
    }
  }
}
