package com.shootr.mobile.ui.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageView169 extends ImageView {

    public static final int WIDTH_RATIO = 16;
    public static final int HEIGHT_RATIO = 9;

    public ImageView169(Context context) {
        super(context);
    }

    public ImageView169(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageView169(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImageView169(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int calculatedHeight = width * HEIGHT_RATIO / WIDTH_RATIO;
        setMeasuredDimension(width, calculatedHeight);
    }
}
