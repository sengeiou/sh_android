package com.shootr.mobile.ui.widgets;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class EndOffsetItemDecoration extends RecyclerView.ItemDecoration {

    private int mOffsetPx;
    private Drawable mOffsetDrawable;
    private int mOrientation;

    /**
     * Constructor that takes in the size of the offset to be added to the end
     * of the RecyclerView.
     *
     * @param offsetPx The size of the offset to be added to the end of the
     * RecyclerView in pixels
     */
    public EndOffsetItemDecoration(int offsetPx) {
        mOffsetPx = offsetPx;
    }

    /**
     * Determines the size and location of the offset to be added to the end
     * of the RecyclerView.
     *
     * @param outRect The {@link Rect} of offsets to be added around the child view
     * @param view The child view to be decorated with an offset
     * @param parent The RecyclerView onto which dividers are being added
     * @param state The current RecyclerView.State of the RecyclerView
     */
    @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int itemCount = state.getItemCount();
        if (parent.getChildAdapterPosition(view) != itemCount - 1) {
            return;
        }

        mOrientation = ((LinearLayoutManager) parent.getLayoutManager()).getOrientation();
        if (mOrientation == LinearLayoutManager.HORIZONTAL) {
            if (mOffsetPx > 0) {
                outRect.right = mOffsetPx;
            } else if (mOffsetDrawable != null) {
                outRect.right = mOffsetDrawable.getIntrinsicWidth();
            }
        } else if (mOrientation == LinearLayoutManager.VERTICAL) {
            if (mOffsetPx > 0) {
                outRect.bottom = mOffsetPx;
            } else if (mOffsetDrawable != null) {
                outRect.bottom = mOffsetDrawable.getIntrinsicHeight();
            }
        }
    }

    /**
     * Draws horizontal or vertical offset onto the end of the parent
     * RecyclerView.
     *
     * @param c The {@link Canvas} onto which an offset will be drawn
     * @param parent The RecyclerView onto which an offset is being added
     * @param state The current RecyclerView.State of the RecyclerView
     */
    @Override public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (mOffsetDrawable == null) {
            return;
        }

        if (mOrientation == LinearLayoutManager.HORIZONTAL) {
            drawOffsetHorizontal(c, parent);
        } else if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawOffsetVertical(c, parent);
        }
    }

    private void drawOffsetHorizontal(Canvas canvas, RecyclerView parent) {
        int parentTop = parent.getPaddingTop();
        int parentBottom = parent.getHeight() - parent.getPaddingBottom();

        View lastChild = parent.getChildAt(parent.getChildCount() - 1);
        RecyclerView.LayoutParams lastChildLayoutParams = (RecyclerView.LayoutParams) lastChild.getLayoutParams();
        int offsetDrawableLeft = lastChild.getRight() + lastChildLayoutParams.rightMargin;
        int offsetDrawableRight = offsetDrawableLeft + mOffsetDrawable.getIntrinsicWidth();

        mOffsetDrawable.setBounds(offsetDrawableLeft, parentTop, offsetDrawableRight, parentBottom);
        mOffsetDrawable.draw(canvas);
    }

    private void drawOffsetVertical(Canvas canvas, RecyclerView parent) {
        int parentLeft = parent.getPaddingLeft();
        int parentRight = parent.getWidth() - parent.getPaddingRight();

        View lastChild = parent.getChildAt(parent.getChildCount() - 1);
        RecyclerView.LayoutParams lastChildLayoutParams = (RecyclerView.LayoutParams) lastChild.getLayoutParams();
        int offsetDrawableTop = lastChild.getBottom() + lastChildLayoutParams.bottomMargin;
        int offsetDrawableBottom = offsetDrawableTop + mOffsetDrawable.getIntrinsicHeight();

        mOffsetDrawable.setBounds(parentLeft, offsetDrawableTop, parentRight, offsetDrawableBottom);
        mOffsetDrawable.draw(canvas);
    }
}
