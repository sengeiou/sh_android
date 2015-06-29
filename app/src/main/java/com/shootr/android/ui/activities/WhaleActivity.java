package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseActivity;

public class WhaleActivity extends BaseActivity {

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, WhaleActivity.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_whale;
    }

    @Override
    protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.inject(this);
    }

    @Override
    protected void initializePresenter() {
        /* no-op */
    }
}
