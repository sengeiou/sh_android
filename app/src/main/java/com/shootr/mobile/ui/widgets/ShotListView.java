package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.TimelineAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnShotClick;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;

public class ShotListView extends LinearLayout {

    private OnShotClick onShotClick;
    private OnShotLongClick onShotLongClick;
    private TimelineAdapter timelineAdapter;
    private Drawable selectableBackground;

    public ShotListView(Context context) {
        super(context);
        init();
    }

    public ShotListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShotListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        TypedArray a = getContext().getTheme().obtainStyledAttributes(new int[] { R.attr.selectableItemBackground });
        selectableBackground = a.getDrawable(0);
        a.recycle();
    }

    public void setAdapter(TimelineAdapter adapter) {
        timelineAdapter = adapter;
        timelineAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override public void onChanged() {
                renderShots();
            }
        });
        timelineAdapter.notifyDataSetChanged();
    }

    private void renderShots() {
        this.removeAllViews();
        int itemsCount = timelineAdapter.getCount();
        for (int i = 0; i < itemsCount; i++) {
            final int position = i;
            View itemView = timelineAdapter.getView(i, null, this);
            itemView.setOnClickListener(new OnClickListener() {
                @Override public void onClick(View view) {
                    onShotClick.onShotClick(timelineAdapter.getItem(position));
                }
            });
            itemView.setOnLongClickListener(new OnLongClickListener() {
                @Override public boolean onLongClick(View view) {
                    onShotLongClick.onShotLongClick(timelineAdapter.getItem(position));
                    return false;
                }
            });
            setItemBackgroundRetainPaddings(itemView);
            this.addView(itemView);
        }
    }

    private void setItemBackgroundRetainPaddings(View itemView) {
        int paddingBottom = itemView.getPaddingBottom();
        int paddingLeft = itemView.getPaddingLeft();
        int paddingRight = itemView.getPaddingRight();
        int paddingTop = itemView.getPaddingTop();
        itemView.setBackgroundDrawable(selectableBackground.getConstantState().newDrawable());
        itemView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    public void setOnShotClick(OnShotClick onShotClick) {
        this.onShotClick = onShotClick;
    }

    public void setOnShotLongClick(OnShotLongClick onShotLongClick) {
        this.onShotLongClick = onShotLongClick;
    }
}
