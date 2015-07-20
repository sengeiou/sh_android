package com.shootr.android.ui.activities;

import android.os.Bundle;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;

public class SupportActivity extends BaseToolbarDecoratedActivity {

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_support;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        //TODO
    }

    @Override protected void initializePresenter() {
        // TODO if really procceed
    }
}
