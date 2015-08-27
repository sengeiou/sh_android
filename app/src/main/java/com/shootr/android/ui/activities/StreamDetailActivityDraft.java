package com.shootr.android.ui.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.base.BaseActivity;

public class StreamDetailActivityDraft extends BaseToolbarDecoratedActivity {

    @Override
    protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_stream_detail_draft;
    }

    @Override
    protected void initializeViews(Bundle savedInstanceState) {
    }

    @Override
    protected void initializePresenter() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shot_detail, menu);
        return true;
    }
}
