package com.shootr.android.ui.widgets;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import com.shootr.android.R;

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

    private void init() {
        setBackgroundTintList(getResources().getColorStateList(R.color.primary));
    }
}
