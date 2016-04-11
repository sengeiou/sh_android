package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;

public class NiceButtonView extends CheckableImageView {

    public NiceButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NiceButtonView(Context context) {
        super(context);
    }

    @Override public void setChecked(boolean b) {
        super.setChecked(b);
        setEnabled(true);
    }

    @Override public boolean performClick() {
        toggle();
        setEnabled(false);
        return super.performClick();
    }
}
