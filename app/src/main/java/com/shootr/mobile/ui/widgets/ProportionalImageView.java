package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ProportionalImageView extends ImageView {

  public static float radius = 18.0f;

  public ProportionalImageView(Context context) {
    super(context);
  }

  public ProportionalImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ProportionalImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
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
    Drawable d = getDrawable();
    if (d != null) {

      int maxSize = MeasureSpec.getSize(widthMeasureSpec);

      if (d.getIntrinsicHeight() > d.getIntrinsicWidth()) {
        int width = d.getIntrinsicWidth() * maxSize / d.getIntrinsicHeight();
        setMeasuredDimension(width, maxSize);
      } else {
        int height = d.getIntrinsicHeight() * maxSize / d.getIntrinsicWidth();
        setMeasuredDimension(maxSize, height);
      }
    } else {
      super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
  }
}
