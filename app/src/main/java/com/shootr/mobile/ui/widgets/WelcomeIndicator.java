package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.shootr.mobile.R;

/**
 * TODO: document your custom view class.
 */
public class WelcomeIndicator extends LinearLayout {

    private SparseArray<View> mIndicators;
    private int mCurrentActivePosition;

    private int mColorActive;
    private int mColorInactive;

    public WelcomeIndicator(Context context) {
        super(context);
        init();
    }

    public WelcomeIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WelcomeIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mColorActive = getResources().getColor(R.color.accent);
        mColorInactive = getResources().getColor(R.color.gray_70);
        if (isInEditMode()) {
            setItemCount(4);
            setActiveItem(0);
        }
    }

    public void setItemCount(int count) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mIndicators = new SparseArray<View>(count);
        mCurrentActivePosition = -1;
        for (int i = 0; i < count; i++) {
            View item = inflater.inflate(R.layout.welcome_indicator_item, this, false);
            this.addView(item);
            mIndicators.put(i, item);
        }
    }

    public void setActiveItem(int position) {
        if (mCurrentActivePosition >= 0) {
            mIndicators.get(mCurrentActivePosition).setBackgroundColor(mColorInactive);
        }
        mIndicators.get(position).setBackgroundColor(mColorActive);
        mCurrentActivePosition = position;
    }
}
