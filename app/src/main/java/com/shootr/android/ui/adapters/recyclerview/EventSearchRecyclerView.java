package com.shootr.android.ui.adapters.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class EventSearchRecyclerView extends RecyclerView {

    private static final String TAG = "EventSearchRecyclerView";

    public EventSearchRecyclerView(android.content.Context context) {
        super(context);
    }

    public EventSearchRecyclerView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public EventSearchRecyclerView(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void scrollTo(int x, int y) {
        Log.e(TAG, "EventSearchRecyclerView does not support scrolling to an absolute position.");
    }

}
