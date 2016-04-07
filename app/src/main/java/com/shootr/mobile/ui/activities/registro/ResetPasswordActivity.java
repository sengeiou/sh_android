package com.shootr.mobile.ui.activities.registro;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.activities.BaseToolbarDecoratedActivity;
import com.shootr.mobile.ui.model.ForgotPasswordUserModel;
import com.shootr.mobile.ui.presenter.ResetPasswordConfirmationPresenter;
import com.shootr.mobile.ui.presenter.ResetPasswordRequestPresenter;
import com.shootr.mobile.ui.views.ResetPasswordConfirmationView;
import com.shootr.mobile.ui.views.ResetPasswordRequestView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class ResetPasswordActivity extends BaseToolbarDecoratedActivity {

    @Bind(R.id.reset_password_request_view) View resetPasswordRequestLayout;
    @Bind(R.id.reset_password_confirmation_view) View resetPasswordConfirmationLayout;
    @BindString(R.string.analytics_screen_forgot_password) String analyticsScreenForgotPassword;

    @Inject ImageLoader imageLoader;
    @Inject ResetPasswordRequestPresenter resetPasswordRequestPresenter;
    @Inject ResetPasswordConfirmationPresenter resetPasswordConfirmationPresenter;
    @Inject FeedbackMessage feedbackMessage;
    @Inject AnalyticsTool analyticsTool;

    private ResetPasswordRequestView resetPasswordRequestView;
    private ResetPasswordConfirmationView resetPasswordConfirmationView;

    //region Initialization
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_reset_password;
    }

    @Override
    protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        analyticsTool.analyticsStart(getBaseContext(), analyticsScreenForgotPassword);
        setupViewImplementations();
        resetPasswordRequestLayout.setVisibility(View.VISIBLE);
        resetPasswordConfirmationLayout.setVisibility(View.GONE);
    }

    private void setupViewImplementations() {
        resetPasswordRequestView = new InnerResetPasswordRequestView();
        ButterKnife.bind(resetPasswordRequestView, resetPasswordRequestLayout);

        resetPasswordConfirmationView = new InnerResetPasswordConfirmationView();
        ButterKnife.bind(resetPasswordConfirmationView, resetPasswordConfirmationLayout);
    }

    @Override
    protected void initializePresenter() {
        resetPasswordRequestPresenter.initialize(resetPasswordRequestView);
    }

    @Override
    protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override
    protected boolean requiresUserLogin() {
        return false;
    }
    //endregion

    //region Activity lifecycle
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

    @OnTextChanged(R.id.reset_password_username_email)
    public void onInputTextChanged(CharSequence usernameOrEmail) {
        resetPasswordRequestPresenter.onUsernameOrEmailChanged(usernameOrEmail.toString());
    }

    @OnClick(R.id.reset_password_next)
    public void onNextClicked() {
        resetPasswordRequestPresenter.next();
    }

    @OnClick(R.id.reset_password_confirm)
    public void onConfirmClicked() {
        resetPasswordConfirmationPresenter.confirm();
    }

    @OnClick(R.id.reset_password_done)
    public void onDoneClicked() {
        resetPasswordConfirmationPresenter.done();
    }

    private void transitionToConfirmationView(ForgotPasswordUserModel forgotPasswordUserModel) {
        resetPasswordRequestLayout.setVisibility(View.GONE);
        resetPasswordConfirmationLayout.setVisibility(View.VISIBLE);
        resetPasswordConfirmationPresenter.initialize(resetPasswordConfirmationView, forgotPasswordUserModel);
    }

    private void closeScreenAndLaunchLogin() {
        finish();
    }

    public class InnerResetPasswordRequestView implements ResetPasswordRequestView {

        @Bind(R.id.reset_password_username_email) EditText usernameOrEmailInput;
        @Bind(R.id.reset_password_next) View nextButton;
        @Bind(R.id.reset_password_progress) View progressView;
        @Bind(R.id.reset_password_error_message) TextView resetPasswordError;

        @Override
        public void enableNextButton() {
            nextButton.setEnabled(true);
        }

        @Override
        public void disableNextButton() {
            nextButton.setEnabled(false);
        }

        @Override
        public void navigateToResetPasswordConfirmation(ForgotPasswordUserModel forgotPasswordUserModel) {
            transitionToConfirmationView(forgotPasswordUserModel);
        }

        @Override
        public void showResetPasswordError() {
            resetPasswordError.setVisibility(View.VISIBLE);
        }

        @Override
        public void hideKeyboard() {
            InputMethodManager imm = (InputMethodManager) ResetPasswordActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(usernameOrEmailInput.getWindowToken(), 0);
        }

        @Override
        public String getUsernameOrEmail() {
            return usernameOrEmailInput.getText().toString();
        }

        @Override
        public void showLoading() {
            progressView.setVisibility(View.VISIBLE);
        }

        @Override
        public void hideLoading() {
            progressView.setVisibility(View.GONE);
        }

        @Override
        public void showError(String message) {
            feedbackMessage.show(getView(), message);
        }
    }

    public class InnerResetPasswordConfirmationView implements ResetPasswordConfirmationView {

        @Bind(R.id.reset_password_avatar) ImageView avatar;
        @Bind(R.id.reset_password_username) TextView usernameText;
        @Bind(R.id.reset_password_email_confirmation_message) TextView confirmationMessage;
        @Bind(R.id.reset_password_confirm) View confirmButton;
        @Bind(R.id.reset_password_done) View doneButton;
        @Bind(R.id.reset_password_progress) View progressView;

        @Override
        public void showAvatar(String avatarUrl) {
            imageLoader.loadProfilePhoto(avatarUrl, avatar);
        }

        @Override
        public void showUsername(String username) {
            usernameText.setText(username);
        }

        @Override
        public void hideConfirmationButton() {
            confirmButton.setVisibility(View.GONE);
        }

        @Override
        public void showConfirmationButton() {
            confirmButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void showDoneButton() {
            doneButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void showPostConfirmationMessage(String email) {
            confirmationMessage.setVisibility(View.VISIBLE);
            confirmationMessage.setText(getString(R.string.reset_password_confirmation_message_email_pattern, email));
        }

        @Override
        public void navigateToLogin() {
            closeScreenAndLaunchLogin();
        }

        @Override
        public void showLoading() {
            progressView.setVisibility(View.VISIBLE);
        }

        @Override
        public void hideLoading() {
            progressView.setVisibility(View.GONE);
        }

        @Override
        public void showError(String message) {
            feedbackMessage.show(getView(), message);
        }
    }

    @Override protected void onPause() {
        super.onPause();
        analyticsTool.analyticsStop(getBaseContext(), this);
    }
}
