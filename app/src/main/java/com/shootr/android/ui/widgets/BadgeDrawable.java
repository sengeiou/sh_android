package com.shootr.android.ui.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import com.shootr.android.R;

public class BadgeDrawable extends Drawable {

    private Paint mBadgePaint;
    private Paint mBadgePaintBorder;
    private Paint mTextPaint;
    private Rect mTxtRect = new Rect();

    private String mCount = "";
    private boolean mWillDraw = false;

    public BadgeDrawable(Context context){
        final Resources res = context.getResources();

        mBadgePaint = new Paint();
        mBadgePaint.setColor(res.getColor(R.color.white));
        mBadgePaint.setAntiAlias(true);
        mBadgePaint.setStyle(Paint.Style.FILL);

        mBadgePaintBorder = new Paint();
        mBadgePaintBorder.setColor(res.getColor(R.color.primary));
        mBadgePaintBorder.setStyle(Paint.Style.STROKE);
        mBadgePaintBorder.setAntiAlias(true);

        mBadgePaintBorder.setStrokeWidth(res.getDimension(R.dimen.badge_stroke_width));

        mTextPaint = new Paint();
        mTextPaint.setColor(res.getColor(R.color.primary));
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setTextSize(res.getDimension(R.dimen.badge_text_size));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override public void draw(Canvas canvas) {
        if(!mWillDraw){
            return;
        }
        Rect bounds = getBounds();
        float width = bounds.right-bounds.left;
        float height = bounds.bottom-bounds.top;
        // Position the badge in the top-right quadrant of the icon.
        float radius = ((Math.min(width, height) / 2) - 1) / 2;
        float centerX = width - radius - 1;
        float centerY = radius + 1;

        // Draw badge circle.

        canvas.drawCircle(centerX, centerY, radius, mBadgePaint);
        canvas.drawCircle(centerX, centerY, radius, mBadgePaintBorder);

        // Draw badge count text inside the circle.
        mTextPaint.getTextBounds(mCount, 0, mCount.length(), mTxtRect);
        float textHeight = mTxtRect.bottom - mTxtRect.top;
        float textY = centerY + (textHeight / 2f);
        canvas.drawText(mCount, centerX, textY, mTextPaint);
    }

    /*
    Sets the count (i.e notifications) to display.
     */
    public void setCount(int count) {
        mCount = Integer.toString(count);

        // Only draw a badge if there are notifications.
        mWillDraw = count > 0;
        invalidateSelf();
    }

    @Override public void setAlpha(int alpha) {
        /* no-op */
    }

    @Override public void setColorFilter(ColorFilter cf) {
        /* no-op */
    }

    @Override public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override public ConstantState getConstantState() {
        return new ConstantState() {
            @Override public Drawable newDrawable() {
                return BadgeDrawable.this;
            }

            @Override public int getChangingConfigurations() {
                return 0;
            }
        };
    }
}
