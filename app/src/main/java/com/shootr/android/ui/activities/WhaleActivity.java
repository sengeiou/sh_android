package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseActivity;

public class WhaleActivity extends BaseActivity {

    private static final String EXTRA_MESSAGE = "message";
    private static final String EXTRA_TITLE = "title";

    @InjectView(R.id.whale_title) TextView whaleTitle;
    @InjectView(R.id.whale_message) TextView whaleMessage;

    public static Intent newIntent(@NonNull Context context, @NonNull String title, @NonNull String message) {
        Intent intent = new Intent(context, WhaleActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_MESSAGE, message);
        return intent;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_whale;
    }

    @Override
    protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.inject(this);
        String message = getIntent().getStringExtra(EXTRA_MESSAGE);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        whaleTitle.setText(title);
        whaleMessage.setText(message);
    }

    @Override
    protected void initializePresenter() {
        /* no-op */
    }
}
