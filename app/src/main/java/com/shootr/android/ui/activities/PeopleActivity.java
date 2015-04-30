package com.shootr.android.ui.activities;

import android.os.Bundle;
import com.shootr.android.R;
import com.shootr.android.ui.NavigationDrawerDecorator;

public class PeopleActivity extends BaseNavDrawerToolbarActivity {

    @Override protected int getNavDrawerItemId() {
        return NavigationDrawerDecorator.NAVDRAWER_ITEM_PEOPLE;
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_people;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        /* no-op: Framgent in the layout */
    }

    @Override protected void initializePresenter() {
        /* no-op */
    }

}
