package com.shootr.mobile.ui.widgets;

/**
 * Created by miniserver on 22/3/18.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

import static com.shootr.mobile.ShootrApplication.SCREEN_SIZE;

public class VideoFrameView extends FrameLayout {

  private static final float RADIUS = 15.0f;

  private boolean justTopCorners;
  private float[] mRadii = new float[] { 0, 0, 0, 0, 0, 0, 0, 0 };

  private int maxWidth;

  public VideoFrameView(Context context) {
    super(context);
    setupMaxWidht();
  }

  public VideoFrameView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setupMaxWidht();
  }

  public VideoFrameView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    setupMaxWidht();
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

  @Override protected void onDraw(Canvas canvas) {
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

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int heightIndp = 9 * maxWidth / 16;
    super.onMeasure(MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(heightIndp, MeasureSpec.EXACTLY));
  }

  private void setupMaxWidht() {
    int avatarImageSize = (int) dp(36);
    int avatarImageMargin = (int) dp(8);
    int conatinerPadding = (int) dp(70);

    int imageOffset = avatarImageMargin + avatarImageSize + conatinerPadding;

    maxWidth = SCREEN_SIZE.x - imageOffset;
  }

  private int convertDpToPx(int dp) {
    return Math.round(
        dp * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
  }

  private float dp(float dp) {
    float scale = getResources().getDisplayMetrics().density;
    return (int) (dp * scale + 0.5f);
  }
}
