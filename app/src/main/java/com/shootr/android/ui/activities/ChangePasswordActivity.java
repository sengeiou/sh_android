package com.shootr.android.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.activities.registro.LoginSelectionActivity;
import com.shootr.android.ui.presenter.ChangePasswordPresenter;
import com.shootr.android.ui.views.ChangePasswordView;
import com.shootr.android.util.FeedbackLoader;
import javax.inject.Inject;

public class ChangePasswordActivity extends BaseToolbarDecoratedActivity implements ChangePasswordView {

    public static final int LOGOUT_DISMISS_DELAY = 1500;

    private ProgressDialog progress;

    @Bind(R.id.current_password) EditText currentPasswordInput;
    @Bind(R.id.new_password) EditText newPasswordInput;
    @Bind(R.id.new_password_again) EditText newPasswordAgainInput;
    @Bind(R.id.change_password_button) View changePasswordButton;

    @Inject ChangePasswordPresenter changePasswordPresenter;
    @Inject FeedbackLoader feedbackLoader;

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_change_password;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    @Override protected void initializePresenter() {
        changePasswordPresenter.initialize(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.change_password_button)
    public void onChangePasswordButtonPressed() {
        changePasswordPresenter.attempToChangePassword(currentPasswordInput.getText().toString(),
          newPasswordInput.getText().toString(), newPasswordAgainInput.getText().toString());
    }

    @Override public void showCurrentPasswordError(String errorMessage) {
        currentPasswordInput.setError(errorMessage);
    }

    @Override public void showNewPasswordError(String errorMessage) {
        newPasswordInput.setError(errorMessage);
    }

    @Override public void showNewPasswordAgainError(String errorMessage) {
        newPasswordAgainInput.setError(errorMessage);
    }

    @Override public void showError(String errorMessage) {
        feedbackLoader.showLongFeedback(getView(), errorMessage);
    }

    @Override public void navigateToWelcomeScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                hideLogoutInProgress();
                redirectToWelcome();
            }
        }, LOGOUT_DISMISS_DELAY);
    }

    @Override public void showLogoutInProgress() {
        progress = ProgressDialog.show(this,
          null,
          getString(R.string.change_password_logout_message),
          true);
    }

    @Override public void hideLogoutInProgress() {
        progress.dismiss();
    }

    private void redirectToWelcome() {
        Intent intent = new Intent(this, LoginSelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
