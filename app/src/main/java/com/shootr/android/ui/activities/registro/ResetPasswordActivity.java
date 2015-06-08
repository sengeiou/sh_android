package com.shootr.android.ui.activities.registro;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.activities.BaseToolbarDecoratedActivity;
import com.shootr.android.ui.model.ForgotPasswordUserModel;
import com.shootr.android.ui.presenter.ResetPasswordConfirmationPresenter;
import com.shootr.android.ui.presenter.ResetPasswordRequestPresenter;
import com.shootr.android.ui.views.ResetPasswordConfirmationView;
import com.shootr.android.ui.views.ResetPasswordRequestView;
import com.shootr.android.util.PicassoWrapper;
import javax.inject.Inject;

public class ResetPasswordActivity extends BaseToolbarDecoratedActivity {

    @InjectView(R.id.reset_password_request_view) View resetPasswordRequestLayout;
    @InjectView(R.id.reset_password_confirmation_view) View resetPasswordConfirmationLayout;

    @Inject PicassoWrapper picasso;
    @Inject ResetPasswordRequestPresenter resetPasswordRequestPresenter;
    @Inject ResetPasswordConfirmationPresenter resetPasswordConfirmationPresenter;

    private ResetPasswordRequestView resetPasswordRequestView;
    private ResetPasswordConfirmationView resetPasswordConfirmationView;

    //region Initialization
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_reset_password;
    }

    @Override
    protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.inject(this);
        setupViewImplementations();
        resetPasswordRequestLayout.setVisibility(View.VISIBLE);
        resetPasswordConfirmationLayout.setVisibility(View.GONE);
    }

    private void setupViewImplementations() {
        resetPasswordRequestView = new InnerResetPasswordRequestView();
        ButterKnife.inject(resetPasswordRequestView, resetPasswordRequestLayout);

        resetPasswordConfirmationView = new InnerResetPasswordConfirmationView();
        ButterKnife.inject(resetPasswordConfirmationView, resetPasswordConfirmationLayout);
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
        //TODO con animacion de transicion
        resetPasswordRequestLayout.setVisibility(View.GONE);
        resetPasswordConfirmationLayout.setVisibility(View.VISIBLE);
        resetPasswordConfirmationPresenter.initialize(resetPasswordConfirmationView, forgotPasswordUserModel);
    }

    private void closeScreenAndLaunchLogin() {
        finish();
        //TODO open login
    }

    public class InnerResetPasswordRequestView implements ResetPasswordRequestView {

        @InjectView(R.id.reset_password_username_email) EditText usernameOrEmailInput;
        @InjectView(R.id.reset_password_next) View nextButton;
        @InjectView(R.id.reset_password_progress) View progressView;
        @InjectView(R.id.reset_password_error_message) TextView resetPasswordError;

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
            Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }

    public class InnerResetPasswordConfirmationView implements ResetPasswordConfirmationView {

        @InjectView(R.id.reset_password_avatar) ImageView avatar;
        @InjectView(R.id.reset_password_username) TextView usernameText;
        @InjectView(R.id.reset_password_email_confirmation_message) TextView confirmationMessage;
        @InjectView(R.id.reset_password_confirm) View confirmButton;
        @InjectView(R.id.reset_password_done) View doneButton;

        @Override
        public void showAvatar(String avatarUrl) {
            picasso.loadProfilePhoto(avatarUrl).into(avatar);
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
            //TODO
        }

        @Override
        public void hideLoading() {
            //TODO
        }

        @Override
        public void showError(String message) {
            //TODO
        }
    }
}
