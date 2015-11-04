package com.shootr.mobile.ui.activities.registro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.utils.LocaleProvider;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.activities.BaseToolbarDecoratedActivity;
import com.shootr.mobile.ui.activities.WelcomePageActivity;
import com.shootr.mobile.ui.presenter.EmailRegistrationPresenter;
import com.shootr.mobile.ui.views.EmailRegistrationView;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.IntentFactory;
import com.shootr.mobile.util.Intents;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;

public class EmailRegistrationActivity extends BaseToolbarDecoratedActivity implements EmailRegistrationView {

    @Bind(R.id.registration_email) EditText emailInput;
    @Bind(R.id.registration_username) EditText usernameInput;
    @Bind(R.id.registration_password) EditText passwordInput;
    @Bind(R.id.registration_create_button) View createButton;
    @Bind(R.id.registration_create_progress) View progress;
    @Bind(R.id.registration_legal_disclaimer) TextView disclaimer;

    @BindString(R.string.terms_of_service_base_url) String termsOfServiceBaseUrl;
    @BindString(R.string.privay_policy_service_base_url) String privacyPolicyServiceBaseUrl;

    @Inject EmailRegistrationPresenter presenter;
    @Inject LocaleProvider localeProvider;
    @Inject FeedbackMessage feedbackMessage;
    @Inject IntentFactory intentFactory;

    //region Initialization
    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_registration_email;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setupDisclaimerLinks();
    }

    private void setupDisclaimerLinks() {
        String originalDisclaimerText = getString(R.string.activity_registration_legal_disclaimer);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(originalDisclaimerText);

        String termsPatternText = "\\(terms-of-service\\)";
        String termsText = getString(R.string.activity_registration_legal_disclaimer_terms_of_service);
        final View.OnClickListener termsClickListener = new View.OnClickListener() {
            @Override public void onClick(View v) {
                String termsUrl = String.format(termsOfServiceBaseUrl, localeProvider.getLanguage());
                Intent termsIntent = intentFactory.openEmbededUrlIntent(EmailRegistrationActivity.this, termsUrl);
                Intents.maybeStartActivity(EmailRegistrationActivity.this, termsIntent);
            }
        };
        replacePatternWithClickableText(spannableStringBuilder, termsPatternText, termsText, termsClickListener);

        String privacyPatternText = "\\(privacy-policy\\)";
        String privacyText = getString(R.string.activity_registration_legal_disclaimer_privacy_policy);
        final View.OnClickListener privacyClickListener = new View.OnClickListener() {
            @Override public void onClick(View v) {
                String privacyUrl = String.format(privacyPolicyServiceBaseUrl, localeProvider.getLanguage());
                Intent privacyIntent = intentFactory.openEmbededUrlIntent(EmailRegistrationActivity.this, privacyUrl);
                Intents.maybeStartActivity(EmailRegistrationActivity.this, privacyIntent);
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
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }

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
        feedbackMessage.show(getView(), message);
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
                  presenter.confirmAccountCreation();
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
        finish();
        Intent navigateToWelcomePageIntent = new Intent(this, WelcomePageActivity.class);
        navigateToWelcomePageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(navigateToWelcomePageIntent);
    }
    //endregion
}