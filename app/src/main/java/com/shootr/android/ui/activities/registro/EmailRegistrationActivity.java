package com.shootr.android.ui.activities.registro;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
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

    @OnClick(R.id.registration_create_button) //
    public void onCreateAccountClick() {
        presenter.onCreateAccount();
    }

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
        return email.getText().toString();
    }

    @Override public String getUsername() {
        return username.getText().toString();
    }

    @Override public String getPassword() {
        return password.getText().toString();
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
}
