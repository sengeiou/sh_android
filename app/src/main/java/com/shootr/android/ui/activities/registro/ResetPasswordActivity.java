package com.shootr.android.ui.activities.registro;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.dd.CircularProgressButton;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.activities.BaseToolbarDecoratedActivity;
import com.shootr.android.ui.presenter.ResetPasswordPresenter;
import com.shootr.android.ui.views.ResetPasswordView;
import javax.inject.Inject;

public class ResetPasswordActivity extends BaseToolbarDecoratedActivity implements ResetPasswordView {

    private static final int BUTTON_LOADING = 1;
    private static final int BUTTON_NORMAL = 0;
    private static final int BUTTON_ERROR = -1;

    @InjectView(R.id.reset_password_username_email) AutoCompleteTextView emailUsername;
    @InjectView(R.id.reset_password_button) CircularProgressButton resetButton;

    @Inject ResetPasswordPresenter resetPasswordPresenter;

    @Override protected int getLayoutResource() {
        return R.layout.activity_reset_password;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.inject(this);
    }

    @Override protected void initializePresenter() {
        resetPasswordPresenter.initialize(this);
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected boolean requiresUserLogin() {
        return false;
    }

    @OnTextChanged(R.id.reset_password_username_email)
    public void inputTextChanged() {
        resetPasswordPresenter.inputTextChanged();
    }

    @OnClick(R.id.reset_password_button)
    public void onResetButtonClick(){
        resetPasswordPresenter.attempReset();
    }

    @Override public void disableResetButton() {
        resetButton.setEnabled(false);
    }

    @Override public void enableResetButton() {
        resetButton.setEnabled(true);
    }

    @Override public String getUsernameOrEmail() {
        return emailUsername.getText().toString();
    }

    @Override public void showLoading() {
        resetButton.setIndeterminateProgressMode(true);
        resetButton.setProgress(BUTTON_LOADING);
    }

    @Override public void hideLogin() {
        resetButton.setProgress(BUTTON_NORMAL);
    }

    @Override public void showError() {
        resetButton.setProgress(BUTTON_ERROR);
    }
}
