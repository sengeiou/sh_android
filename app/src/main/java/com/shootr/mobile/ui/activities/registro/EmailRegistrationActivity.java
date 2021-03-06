package com.shootr.mobile.ui.activities.registro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import com.shootr.mobile.R;
import com.shootr.mobile.data.prefs.ActivityShowcase;
import com.shootr.mobile.data.prefs.BooleanPreference;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.activities.BaseToolbarDecoratedActivity;
import com.shootr.mobile.ui.activities.OnBoardingStreamActivity;
import com.shootr.mobile.ui.presenter.EmailRegistrationPresenter;
import com.shootr.mobile.ui.views.EmailRegistrationView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.FeedbackMessage;
import javax.inject.Inject;

public class EmailRegistrationActivity extends BaseToolbarDecoratedActivity implements EmailRegistrationView {

    private static final int PRIVACY_LAWS = 1;

    @BindView(R.id.registration_email) EditText emailInput;
    @BindView(R.id.registration_username) EditText usernameInput;
    @BindView(R.id.registration_password) EditText passwordInput;
    @BindView(R.id.registration_create_button) View createButton;
    @BindView(R.id.registration_create_progress) View progress;

    @BindString(R.string.analytics_action_signup) String analyticsActionSignup;
    @BindString(R.string.analytics_label_signup) String analyticsLabelSignup;

    @Inject EmailRegistrationPresenter presenter;
    @Inject FeedbackMessage feedbackMessage;
    @Inject AnalyticsTool analyticsTool;
    @Inject SessionRepository sessionRepository;
    @Inject @ActivityShowcase BooleanPreference activityShowcase;

    //region Initialization
    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_registration_email;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    @Override protected void initializePresenter() {
        presenter.initialize(this);
    }

    @Override protected boolean requiresUserLogin() {
        return false;
    }
    //endregion

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.registration_create_button)
    public void onCreateAccountClick() {
        presenter.createAccount();
    }

    @OnFocusChange(R.id.registration_email) public void onEmailFieldFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            presenter.emailFocusRemoved();
        }
    }

    @OnFocusChange(R.id.registration_username) public void onUsernameFieldFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            presenter.usernameFocusRemoved();
        }
    }

    @OnFocusChange(R.id.registration_password) public void onPasswordFieldFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            presenter.passwordFocusRemoved();
        }
    }

    //region View methods
    @Override public void showLoading() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        progress.setVisibility(View.GONE);
    }

    @Override public void showError(String message) {
        feedbackMessage.show(getView(), message);
    }

    @Override public void showMassiveRegisterError(String message) {
        new AlertDialog.Builder(this).setMessage(message)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
            /* no-op */
                }
            })
            .show();
    }

    @Override public void showCreateButton() {
        createButton.setVisibility(View.VISIBLE);
    }

    @Override public void hideCreateButton() {
        createButton.setVisibility(View.INVISIBLE);
    }

    @Override public String getEmail() {
        String email = this.emailInput.getText().toString();
        return !email.isEmpty() ? email : null;
    }

    @Override public String getUsername() {
        String username = this.usernameInput.getText().toString();
        return !username.isEmpty() ? username : null;
    }

    @Override public String getPassword() {
        String password = this.passwordInput.getText().toString();
        return !password.isEmpty() ? password : null;
    }

    @Override public void showEmailError(String errorMessage) {
        emailInput.setError(errorMessage);
    }

    @Override public void showUsernameError(String errorMessage) {
        usernameInput.setError(errorMessage);
    }

    @Override public void showPasswordError(String errorMessage) {
        passwordInput.setError(errorMessage);
    }

    @Override public void askEmailConfirmation() {
        new AlertDialog.Builder(this).setTitle(getString(R.string.account_confirmation_title))
          .setMessage(getEmail())
          .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  presenter.onConfirmEmail();
              }
          })
          .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  presenter.dontConfirmAccountCreation();
              }
          })
          .show();
    }

    @Override public void focusOnEmailField() {
        emailInput.requestFocus();
    }

    @Override public void focusOnPasswordField() {
        passwordInput.requestFocus();
    }

    @Override public void focusOnUsernameField() {
        usernameInput.requestFocus();
    }

    @Override public void navigateToWelcomePage() {
        analyticsTool.sendSignUpEvent(sessionRepository.getCurrentUser(), analyticsActionSignup, this);
        activityShowcase.set(true);
        finish();
        Intent navigateToWelcomePageIntent = new Intent(this, OnBoardingStreamActivity.class);
        navigateToWelcomePageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(navigateToWelcomePageIntent);
    }

    @Override public void showPrivacyLaws() {
        Intent intent = new Intent(this, PrivacyLawActivity.class);
        startActivityForResult(intent, PRIVACY_LAWS);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            presenter.confirmAccountCreation();
        }
    }

    //endregion
}
