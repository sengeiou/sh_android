package com.shootr.android.ui.activities.registro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.activities.BaseToolbarDecoratedActivity;
import com.shootr.android.ui.presenter.EmailRegistrationPresenter;
import com.shootr.android.ui.views.EmailRegistrationView;
import javax.inject.Inject;

public class EmailRegistrationActivity extends BaseToolbarDecoratedActivity implements EmailRegistrationView {

    @InjectView(R.id.registration_email) EditText email;
    @InjectView(R.id.registration_username) EditText username;
    @InjectView(R.id.registration_password) EditText password;
    @InjectView(R.id.registration_create_button) View createButton;
    @InjectView(R.id.registration_create_progress) View progress;

    @Inject EmailRegistrationPresenter presenter;

    //region Initialization
    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_registration_email;
    }

    @Override protected void initializeViews() {
        ButterKnife.inject(this);
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

    @OnClick(R.id.registration_create_button) //
    public void onCreateAccountClick() {
        presenter.createAccount();
    }

    @OnFocusChange(R.id.registration_email)
    public void onEmailFieldFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            presenter.emailFocusRemoved();
        }
    }

    @OnFocusChange(R.id.registration_username)
    public void onUsernameFieldFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            presenter.usernameFocusRemoved();
        }
    }

    @OnFocusChange(R.id.registration_password)
    public void onPasswordFieldFocusChanged(boolean hasFocus) {
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
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override public void showCreateButton() {
        createButton.setVisibility(View.VISIBLE);
    }

    @Override public void hideCreateButton() {
        createButton.setVisibility(View.INVISIBLE);
    }

    @Override public String getEmail() {
        String email = this.email.getText().toString();
        return !email.isEmpty() ? email : null;
    }

    @Override public String getUsername() {
        String username = this.username.getText().toString();
        return !username.isEmpty() ? username : null;
    }

    @Override public String getPassword() {
        String password = this.password.getText().toString();
        return !password.isEmpty() ? password : null;
    }

    @Override public void showEmailError(String errorMessage) {
        email.setError(errorMessage);
    }

    @Override public void showUsernameError(String errorMessage) {
        username.setError(errorMessage);
    }

    @Override public void showPasswordError(String errorMessage) {
        password.setError(errorMessage);
    }

    @Override public void askEmailConfirmation() {
        new AlertDialog.Builder(this).setTitle(getString(R.string.account_confirmation_title))
          .setMessage(getEmail())
          .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  presenter.confirmAccountCreation();
              }
          })
          .setNegativeButton(R.string.no, null)
          .show();
    }
    //endregion
}
