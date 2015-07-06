package com.shootr.android.ui.activities.registro;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.dd.CircularProgressButton;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.activities.BaseToolbarDecoratedActivity;
import com.shootr.android.ui.activities.MainTabbedActivity;
import com.shootr.android.ui.presenter.EmailLoginPresenter;
import com.shootr.android.ui.views.EmailLoginView;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.inject.Inject;

public class EmailLoginActivity extends BaseToolbarDecoratedActivity implements EmailLoginView {

    private static final int BUTTON_ERROR = -1;
    private static final int BUTTON_NORMAL = 0;
    private static final int BUTTON_LOADING = 1;

    @InjectView(R.id.email_login_username_email) public AutoCompleteTextView emailUsername;
    @InjectView(R.id.email_login_password) public EditText password;
    @InjectView(R.id.email_login_button) CircularProgressButton loginButton;
    @InjectView(R.id.email_login_forgot) TextView resetPassword;

    @Inject EmailLoginPresenter presenter;

    /* --- UI methods --- */

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected  void initializeViews(Bundle savedInstanceState){
        ButterKnife.inject(this);
        setupSuggestedEmails();
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_login_email;
    }

    @Override protected void initializePresenter() {
        presenter.initialize(this);
    }



    @OnTextChanged({R.id.email_login_username_email, R.id.email_login_password})
    public void inputTextChanged() {
        if (presenter.isInitialized()) {
            presenter.inputTextChanged();
        }
    }

    @OnClick(R.id.email_login_button)
    public void onLoginWithEmailButtonClick(){
        presenter.attempLogin();
    }

    @OnClick(R.id.email_login_forgot)
    public void onLoginForgotButtonClick(){
        Intent resetPasswordIntent = new Intent(this, ResetPasswordActivity.class);
        startActivity(resetPasswordIntent);
    }


    public void goToTimeline(){
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
        loginButton.setIndeterminateProgressMode(true);
        loginButton.setProgress(BUTTON_LOADING);
    }

    @Override public void hideLoading() {
        loginButton.setProgress(BUTTON_NORMAL);
    }

    @Override public void showError(String errorMessage) {
        loginButton.setErrorText(errorMessage);
        loginButton.setProgress(BUTTON_ERROR);
    }

    @Override public void disableLoginButton() {
        loginButton.setEnabled(false);
    }

    @Override public void enableLoginButton() {
        loginButton.setEnabled(true);
    }

    @Override public void hideError() {
        loginButton.setProgress(BUTTON_NORMAL);
    }

    @Override protected boolean requiresUserLogin() {
        return false;
    }

    private void setupSuggestedEmails() {
        ArrayAdapter<String> emailSuggestionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getEmailAccounts());
        emailUsername.setAdapter(emailSuggestionAdapter);
    }

    public List<String> getEmailAccounts() {
        List<String> emailAccounts = new ArrayList<String>();
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        for (Account account : AccountManager.get(this).getAccountsByType("com.google")) {
            if (emailPattern.matcher(account.name).matches()) {
                emailAccounts.add(account.name);
            }
        }
        return emailAccounts;
    }


}
