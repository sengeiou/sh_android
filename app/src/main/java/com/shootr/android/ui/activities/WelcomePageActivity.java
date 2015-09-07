package com.shootr.android.ui.activities;

import android.os.Bundle;
import android.view.View;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseActivity;

public class WelcomePageActivity extends BaseActivity {

    @Bind(R.id.button_get_started) View getStartedButton;
    @Bind(R.id.get_started_progress) View loading;

    @Override protected int getLayoutResource() {
        return R.layout.activity_welcome_page;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    @Override protected void initializePresenter() {

    }
}
