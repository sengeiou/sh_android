package com.shootr.mobile.ui.widgets;

/**
 * Created by miniserver on 22/3/18.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import static com.shootr.mobile.ShootrApplication.SCREEN_SIZE;

public class ScalingImageView extends android.support.v7.widget.AppCompatImageView {

  private static final float RADIUS = 15.0f;

  private int initialWidth;
  private int initialHeight;

  private boolean justTopCorners;
  private float[] mRadii = new float[] { 0, 0, 0, 0, 0, 0, 0, 0 };

  private int maxWidht;

  public ScalingImageView(Context context) {
    super(context);
    setScaleType(ScaleType.MATRIX);
    setupMaxWidht();
  }

  public ScalingImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setScaleType(ScaleType.MATRIX);
    setupMaxWidht();
  }

  public ScalingImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    setScaleType(ScaleType.MATRIX);
    setupMaxWidht();
  }

  public void setInitialWidth(int initialWidth) {
    this.initialWidth = initialWidth;
  }

  public void setInitialHeight(int initialHeight) {
    this.initialHeight = initialHeight;
  }



  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    int maxSize = maxWidht;

    if (initialWidth != 0 && initialHeight != 0) {
      if (initialHeight > initialWidth) {
        int width = initialWidth * maxSize / initialHeight;
        setMeasuredDimension(maxSize, maxSize);
      } else if (initialWidth == initialHeight) {
        setMeasuredDimension(maxSize, maxSize);
      } else {
        int height = initialHeight * maxSize / initialWidth;
        setMeasuredDimension(maxSize, height);
      }
    } else {
      super.onMeasure(maxSize, maxSize);
    }

  }

  public void setOnlyTopCorner(boolean justTopCorners) {
    this.justTopCorners = justTopCorners;
    final float density = getResources().getDisplayMetrics().density;
    final float lt = RADIUS * density;
    final float rt = RADIUS * density;
    final float lb = RADIUS * density;
    final float rb = RADIUS * density;

    if (justTopCorners) {
      mRadii = new float[] { lt, lt, rt, rt, 0, 0, 0, 0 };
    } else {
      mRadii = new float[] { lt, lt, rt, rt, rb, rb, lb, lb };
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    try {

      Path clipPath = new Path();
      RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
      clipPath.addRoundRect(rect, mRadii, Path.Direction.CW);
      canvas.clipPath(clipPath);
    } catch (UnsupportedOperationException error) {
      /* no-op */
    }
    super.onDraw(canvas);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    recomputeImgMatrix();
  }

  @Override
  protected boolean setFrame(int l, int t, int r, int b) {
    recomputeImgMatrix();
    return super.setFrame(l, t, r, b);
  }

  private void recomputeImgMatrix() {

    final Drawable drawable = getDrawable();
    if (drawable == null) {
      return;
    }

    final Matrix matrix = getImageMatrix();

    float scale;
    final int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
    final int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
    final int drawableWidth = drawable.getIntrinsicWidth();
    final int drawableHeight = drawable.getIntrinsicHeight();

    if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
      scale = (float) viewHeight / (float) drawableHeight;
    } else {
      scale = (float) viewWidth / (float) drawableWidth;
    }

    matrix.setScale(scale, scale);
    setImageMatrix(matrix);
  }

  private void setupMaxWidht() {
    int avatarImageSize = (int) dp(36);
    int avatarImageMargin = (int) dp(8);
    int conatinerPadding = (int) dp(70);

    int imageOffset =  avatarImageMargin + avatarImageSize + conatinerPadding;

    maxWidht = SCREEN_SIZE.x - imageOffset;

  }

  private float dp(float dp) {
    float scale = getResources().getDisplayMetrics().density;
    return (int) (dp * scale + 0.5f);
  }
}
