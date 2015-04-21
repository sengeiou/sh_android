package com.shootr.android.ui.activities.registro;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;

public class EmailRegistrationActivity extends BaseToolbarDecoratedActivity implements EmailRegistrationView {

    @InjectView(R.id.registration_email) AutoCompleteTextView email;
    @InjectView(R.id.registration_username) EditText username;
    @InjectView(R.id.registration_password) EditText password;
    @InjectView(R.id.registration_create_button) View createButton;
    @InjectView(R.id.registration_create_progress) View progress;
    @InjectView(R.id.registration_legal_disclaimer) TextView disclaimer;

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
        setupDisclaimerLinks();
        setupSuggestedEmails();
    }

    private void setupSuggestedEmails() {
        ArrayAdapter<String> emailSuggestionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getEmailAccounts());
        email.setAdapter(emailSuggestionAdapter);
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


    private void setupDisclaimerLinks() {
        String originalDisclaimerText = getString(R.string.activity_registration_legal_disclaimer);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(originalDisclaimerText);

        String termsPatternText = "\\(terms-of-service\\)";
        String termsText = getString(R.string.activity_registration_legal_disclaimer_terms_of_service);
        final View.OnClickListener termsClickListener = new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(EmailRegistrationActivity.this, "Terms of Service", Toast.LENGTH_SHORT).show();
            }
        };
        replacePatternWithClickableText(spannableStringBuilder, termsPatternText, termsText, termsClickListener);

        String privacyPatternText = "\\(privacy-policy\\)";
        String privacyText = getString(R.string.activity_registration_legal_disclaimer_privacy_policy);
        final View.OnClickListener privacyClickListener = new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(EmailRegistrationActivity.this, "Privacy Policy", Toast.LENGTH_SHORT).show();
            }
        };
        replacePatternWithClickableText(spannableStringBuilder, privacyPatternText, privacyText, privacyClickListener);

        disclaimer.setText(spannableStringBuilder);
        disclaimer.setMovementMethod(new LinkMovementMethod());
    }

    private void replacePatternWithClickableText(SpannableStringBuilder spannableBuilder, String patternText,
      String replaceText, final View.OnClickListener onClick) {
        Pattern termsPattern = Pattern.compile(patternText);
        Matcher termsMatcher = termsPattern.matcher(spannableBuilder.toString());
        if (termsMatcher.find()) {
            int termsStart = termsMatcher.start();
            int termsEnd = termsMatcher.end();
            spannableBuilder.replace(termsStart, termsEnd, replaceText);

            CharacterStyle termsSpan = new ClickableSpan() {
                @Override public void onClick(View widget) {
                    onClick.onClick(widget);
                }
            };
            spannableBuilder.setSpan(termsSpan,
              termsStart,
              termsStart + replaceText.length(),
              Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
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
