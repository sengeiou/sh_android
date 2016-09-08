package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

public class ScrollListView extends ListView implements NestedScrollingChild {

  private OnScrollListener onScrollListener;
  private OnDetectScrollListener onDetectScrollListener;
  private final NestedScrollingChildHelper scrollingChildHelper;

  public ScrollListView(Context context) {
    super(context);
    onCreate(context, null, null);
    scrollingChildHelper = new NestedScrollingChildHelper(this);
    setupNestedScroll();
  }

  public ScrollListView(Context context, AttributeSet attrs) {
    super(context, attrs);
    onCreate(context, attrs, null);
    scrollingChildHelper = new NestedScrollingChildHelper(this);
    setupNestedScroll();
  }

  public ScrollListView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    onCreate(context, attrs, defStyle);
    scrollingChildHelper = new NestedScrollingChildHelper(this);
    setupNestedScroll();
  }

  private void setupNestedScroll() {
    setNestedScrollingEnabled(true);
  }

  @SuppressWarnings("UnusedParameters")
  private void onCreate(Context context, AttributeSet attrs, Integer defStyle) {
    setListeners();
  }

  private void setListeners() {
    super.setOnScrollListener(new OnScrollListener() {

      private int oldTop;
      private int oldFirstVisibleItem;

      @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (onScrollListener != null) {
          onScrollListener.onScrollStateChanged(view, scrollState);
        }
      }

      @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
          int totalItemCount) {
        if (onScrollListener != null) {
          onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

        if (onDetectScrollListener != null) {
          onDetectedListScroll(view, firstVisibleItem);
        }
      }

      private void onDetectedListScroll(AbsListView absListView, int firstVisibleItem) {
        View view = absListView.getChildAt(0);
        int top = (view == null) ? 0 : view.getTop();

        if (firstVisibleItem == oldFirstVisibleItem) {
          if (top > oldTop) {
            onDetectScrollListener.onUpScrolling();
          } else if (top < oldTop) {
            onDetectScrollListener.onDownScrolling();
          }
        } else {
          if (firstVisibleItem < oldFirstVisibleItem) {
            onDetectScrollListener.onUpScrolling();
          } else {
            onDetectScrollListener.onDownScrolling();
          }
        }

        oldTop = top;
        oldFirstVisibleItem = firstVisibleItem;
      }
    });
  }

  @Override public void setOnScrollListener(OnScrollListener onScrollListener) {
    this.onScrollListener = onScrollListener;
  }

  public interface OnDetectScrollListener {

    void onUpScrolling();

    void onDownScrolling();
  }

  @Override public void setNestedScrollingEnabled(boolean enabled) {
    scrollingChildHelper.setNestedScrollingEnabled(enabled);
  }

  @Override public boolean isNestedScrollingEnabled() {
    return scrollingChildHelper.isNestedScrollingEnabled();
  }

  @Override public boolean startNestedScroll(int axes) {
    return scrollingChildHelper.startNestedScroll(axes);
  }

  @Override public void stopNestedScroll() {
    scrollingChildHelper.stopNestedScroll();
  }

  @Override public boolean hasNestedScrollingParent() {
    return scrollingChildHelper.hasNestedScrollingParent();
  }

  @Override public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
      int dyUnconsumed, int[] offsetInWindow) {
    return scrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed,
        dyUnconsumed, offsetInWindow);
  }

  @Override
  public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
    return scrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
  }

  @Override public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
    return scrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
  }

  @Override public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
    return scrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
  }
}
