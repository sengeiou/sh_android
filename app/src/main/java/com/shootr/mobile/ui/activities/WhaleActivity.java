package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.base.BaseActivity;

public class WhaleActivity extends BaseActivity {

    public static Intent newIntent(@NonNull Context context) {
        Intent intent = new Intent(context, WhaleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_whale;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    @Override protected void initializePresenter() {
        /* no-op */
    }

    @OnClick(R.id.whale_ok) public void onRetryClick() {
        finish();
    }

    @Override protected boolean requiresUserLogin() {
        return false;
    }
}
