package com.shootr.mobile.ui.activities.registro;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.activities.BaseToolbarDecoratedActivity;
import com.shootr.mobile.ui.activities.MainTabbedActivity;
import com.shootr.mobile.ui.presenter.EmailLoginPresenter;
import com.shootr.mobile.ui.views.EmailLoginView;
import com.shootr.mobile.util.FeedbackMessage;
import javax.inject.Inject;

public class EmailLoginActivity extends BaseToolbarDecoratedActivity implements EmailLoginView {

    @Bind(R.id.email_login_username_email) public EditText emailUsername;
    @Bind(R.id.email_login_password) public EditText password;
    @Bind(R.id.email_login_button) TextView loginButton;
    @Bind(R.id.login_loading) View loadingView;

    @Inject EmailLoginPresenter presenter;
    @Inject FeedbackMessage feedbackMessage;

    /* --- UI methods --- */

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        loginButton.setEnabled(false);
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_login_email;
    }

    @Override protected void initializePresenter() {
        presenter.initialize(this);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({ R.id.email_login_username_email, R.id.email_login_password }) public void inputTextChanged() {
        if (presenter.isInitialized()) {
            presenter.inputTextChanged();
        }
    }

    @OnClick(R.id.email_login_button) public void onLoginWithEmailButtonClick() {
        presenter.attempLogin();
    }

    @OnClick(R.id.email_login_forgot) public void onLoginForgotButtonClick() {
        Intent resetPasswordIntent = new Intent(this, ResetPasswordActivity.class);
        startActivity(resetPasswordIntent);
    }

    public void goToTimeline() {
        finish();
        Intent i = new Intent(this, MainTabbedActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override public String getUsernameOrEmail() {
        return emailUsername.getText().toString();
    }

    @Override public String getPassword() {
        return password.getText().toString();
    }

    @Override public void showLoading() {
        loginButton.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        loadingView.setVisibility(View.GONE);
        loginButton.setVisibility(View.VISIBLE);
    }

    @Override public void showError(String errorMessage) {
        feedbackMessage.show(getView(), errorMessage);
        loadingView.setVisibility(View.GONE);
        loginButton.setVisibility(View.VISIBLE);
    }

    @Override public void disableLoginButton() {
        loginButton.setEnabled(false);
    }

    @Override public void enableLoginButton() {
        loginButton.setEnabled(true);
    }

    @Override protected boolean requiresUserLogin() {
        return false;
    }
}
