package com.shootr.android.ui.activities;

import com.shootr.android.R;
import com.shootr.android.ui.NavigationDrawerDecorator;

public class PeopleActivity extends BaseNavDrawerToolbarActivity {

    @Override protected int getNavDrawerItemId() {
        return NavigationDrawerDecorator.NAVDRAWER_ITEM_PEOPLE;
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_people;
    }

    @Override protected void initializeViews() {
        /* no-op: Framgent in the layout */
    }

    @Override protected void initializePresenter() {
        /* no-op */
    }

}
