package com.shootr.android.ui.widgets;

import android.content.Context;
import android.view.CollapsibleActionView;
import android.widget.LinearLayout;

public class SearchView extends LinearLayout implements CollapsibleActionView {

    public SearchView(Context context) {
        super(context);
    }

    @Override public void onActionViewExpanded() {
        /* no-op */
    }

    @Override public void onActionViewCollapsed() {
        /* no-op */
    }
}
