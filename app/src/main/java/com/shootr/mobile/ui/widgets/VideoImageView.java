package com.shootr.mobile.ui.widgets;

/**
 * Created by miniserver on 22/3/18.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

public class VideoImageView extends android.support.v7.widget.AppCompatImageView {

  private static final float RADIUS = 15.0f;

  private boolean justTopCorners;
  private float[] mRadii = new float[] { 0, 0, 0, 0, 0, 0, 0, 0 };

  public VideoImageView(Context context) {
    super(context);
  }

  public VideoImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public VideoImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
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
}
