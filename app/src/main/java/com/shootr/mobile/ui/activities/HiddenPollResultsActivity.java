package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.base.BaseActivity;

public class HiddenPollResultsActivity extends BaseActivity {

    private static final String POLL_QUESTION = "pollQuestion";
    @BindView(R.id.poll_question) TextView pollQuestionText;

    @Override protected int getLayoutResource() {
        return R.layout.activity_hide_poll_result;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        pollQuestionText.setText(getIntent().getStringExtra(POLL_QUESTION));
    }

    @Override protected void initializePresenter() {
        /* no-op */
    }

    public static Intent newResultsIntent(Context context, String pollQuestion) {
        Intent intent = new Intent(context, HiddenPollResultsActivity.class);
        intent.putExtra(POLL_QUESTION, pollQuestion);
        return intent;
    }

    @OnClick(R.id.next_button) public void onClick() {
        finish();
    }
}
