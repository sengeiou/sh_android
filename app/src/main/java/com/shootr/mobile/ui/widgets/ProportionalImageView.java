package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ProportionalImageView extends ImageView {

  public static float radius = 18.0f;

  private int initialWidth;
  private int initialHeight;

  public ProportionalImageView(Context context) {
    super(context);
  }

  public ProportionalImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ProportionalImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public void setInitialWidth(int initialWidth) {
    this.initialWidth = initialWidth;
  }

  public void setInitialHeight(int initialHeight) {
    this.initialHeight = initialHeight;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    Path clipPath = new Path();
    RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
    clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW);
    canvas.clipPath(clipPath);
    super.onDraw(canvas);
  }



  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int maxSize = MeasureSpec.getSize(widthMeasureSpec);

    if (initialWidth != 0 && initialHeight != 0) {
      if (initialHeight > initialWidth) {
        int width = initialWidth * maxSize / initialHeight;
        setMeasuredDimension(width, maxSize);
      } else {
        int height = initialHeight * maxSize / initialWidth;
        setMeasuredDimension(maxSize, height);
      }
    } else {
      super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
  }
}
