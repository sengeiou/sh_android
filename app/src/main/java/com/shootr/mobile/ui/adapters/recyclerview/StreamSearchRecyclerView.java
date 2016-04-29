package com.shootr.mobile.ui.adapters.recyclerview;

import android.support.v7.widget.RecyclerView;

public class StreamSearchRecyclerView extends RecyclerView {

    public StreamSearchRecyclerView(android.content.Context context) {
        super(context);
    }

    public StreamSearchRecyclerView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public StreamSearchRecyclerView(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override public void scrollTo(int x, int y) {
        /* super throws exception, ignore it */
    }
}
