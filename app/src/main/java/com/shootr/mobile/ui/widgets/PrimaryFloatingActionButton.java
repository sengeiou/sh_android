package com.shootr.mobile.ui.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import com.shootr.mobile.R;

public class PrimaryFloatingActionButton extends FloatingActionButton {

    public PrimaryFloatingActionButton(Context context) {
        super(context);
        init();
    }

    public PrimaryFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PrimaryFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("NewApi")
    private void init() {
        setBackgroundTintList(getResources().getColorStateList(R.color.primary));
    }
}
