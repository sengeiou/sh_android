package com.shootr.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseActivity;

public class ErrorActivity extends BaseActivity {

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_error;
    }

    @Override
    protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    @Override
    protected void initializePresenter() {
        /* no-op */
    }

    @OnClick(R.id.error_restart)
    public void onRetryClick() {
        Intent restartIntent = new Intent(this, MainTabbedActivity.class);
        restartIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Set this flag
        startActivity(restartIntent);
        finish();
    }
}
