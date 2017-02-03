package com.shootr.mobile.ui.activities.registro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.shootr.mobile.R;
import com.shootr.mobile.data.prefs.BooleanPreference;
import com.shootr.mobile.data.prefs.CurrentUserId;
import com.shootr.mobile.data.prefs.SessionToken;
import com.shootr.mobile.data.prefs.ShouldShowIntro;
import com.shootr.mobile.data.prefs.StringPreference;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.GetStreamInteractor;
import com.shootr.mobile.domain.interactor.user.GetUserByIdInteractor;
import com.shootr.mobile.domain.interactor.user.PerformAutoLoginInteractor;
import com.shootr.mobile.domain.interactor.user.PerformFacebookLoginInteractor;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.utils.LocaleProvider;
import com.shootr.mobile.ui.activities.IntroActivity;
import com.shootr.mobile.ui.activities.MainTabbedActivity;
import com.shootr.mobile.ui.activities.WelcomePageActivity;
import com.shootr.mobile.ui.base.BaseActivity;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.IntentFactory;
import com.shootr.mobile.util.Intents;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import timber.log.Timber;

public class LoginSelectionActivity extends BaseActivity {

  private static final String[] FACEBOOK_PERMISIONS = { "public_profile", "user_friends", "email" };

  @BindView(R.id.login_progress) View loading;
  @BindView(R.id.login_buttons) View buttonsContainer;
  @BindView(R.id.login_selection_legal_disclaimer) TextView disclaimer;

  @BindString(R.string.error_facebook_login) String facebookError;
  @BindString(R.string.error_login_facebook_method) String facebookMethodError;
  @BindString(R.string.terms_of_service_base_url) String termsOfServiceBaseUrl;
  @BindString(R.string.privacy_policy_service_base_url) String privacyPolicyServiceBaseUrl;
  @BindString(R.string.analytics_action_signup) String analyticsActionSignup;
  @BindString(R.string.analytics_label_signup) String analyticsLabelSignup;

  @Inject PerformFacebookLoginInteractor performFacebookLoginInteractor;
  @Inject FeedbackMessage feedbackMessage;
  @Inject @SessionToken StringPreference sessionTokenPreference;
  @Inject @CurrentUserId StringPreference currentUserIdPreference;
  @Inject GetUserByIdInteractor getUserByIdInteractor;
  @Inject GetStreamInteractor getStreamById;
  @Inject @ShouldShowIntro BooleanPreference shouldShowIntro;
  @Inject LocaleProvider localeProvider;
  @Inject IntentFactory intentFactory;
  @Inject SessionRepository sessionRepository;
  @Inject AnalyticsTool analyticsTool;
  @Inject PerformAutoLoginInteractor performAutoLoginInteractor;

  private CallbackManager callbackManager;
  private LoginManager loginManager;

  @Override protected int getLayoutResource() {
    return R.layout.activity_login;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    setupDisclaimerLinks();
    setupStatusBarColor();
  }

  private void setupStatusBarColor() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = getWindow();
      window.setStatusBarColor(getResources().getColor(R.color.primary_dark));
    }
  }

  private void setupDisclaimerLinks() {
    String originalDisclaimerText = getString(R.string.activity_registration_legal_disclaimer);
    SpannableStringBuilder spannableStringBuilder =
        new SpannableStringBuilder(originalDisclaimerText);

    String termsPatternText = "\\(terms-of-service\\)";
    String termsText = getString(R.string.activity_registration_legal_disclaimer_terms_of_service);
    final View.OnClickListener termsClickListener = new View.OnClickListener() {
      @Override public void onClick(View v) {
        showSupportAlertDialog(termsOfServiceClickListener());
      }
    };
    replacePatternWithClickableText(spannableStringBuilder, termsPatternText, termsText,
        termsClickListener);

    String privacyPatternText = "\\(privacy-policy\\)";
    String privacyText = getString(R.string.activity_registration_legal_disclaimer_privacy_policy);
    final View.OnClickListener privacyClickListener = new View.OnClickListener() {
      @Override public void onClick(View v) {
        showSupportAlertDialog(privacyPolicyClickListener());
      }
    };
    replacePatternWithClickableText(spannableStringBuilder, privacyPatternText, privacyText,
        privacyClickListener);

    disclaimer.setText(spannableStringBuilder);
    disclaimer.setMovementMethod(new LinkMovementMethod());
  }

  @NonNull public DialogInterface.OnClickListener privacyPolicyClickListener() {
    return new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        String privacyUrl =
            String.format(privacyPolicyServiceBaseUrl, localeProvider.getLanguage());
        Intent privacyIntent =
            intentFactory.openEmbededUrlIntent(LoginSelectionActivity.this, privacyUrl);
        Intents.maybeStartActivity(LoginSelectionActivity.this, privacyIntent);
      }
    };
  }

  @NonNull public DialogInterface.OnClickListener termsOfServiceClickListener() {
    return new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        String termsUrl = String.format(termsOfServiceBaseUrl, localeProvider.getLanguage());
        Intent termsIntent =
            intentFactory.openEmbededUrlIntent(LoginSelectionActivity.this, termsUrl);
        Intents.maybeStartActivity(LoginSelectionActivity.this, termsIntent);
      }
    };
  }

  private void showSupportAlertDialog(DialogInterface.OnClickListener onClickListener) {
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    alertDialogBuilder //
        .setMessage(getString(R.string.language_support_alert)) //
        .setPositiveButton(getString(R.string.email_confirmation_ok), onClickListener).show();
  }

  private void replacePatternWithClickableText(SpannableStringBuilder spannableBuilder,
      String patternText, String replaceText, final View.OnClickListener onClick) {
    Pattern termsPattern = Pattern.compile(patternText);
    Matcher termsMatcher = termsPattern.matcher(spannableBuilder.toString());
    if (termsMatcher.find()) {
      int termsStart = termsMatcher.start();
      int termsEnd = termsMatcher.end();
      spannableBuilder.replace(termsStart, termsEnd, replaceText);

      CharacterStyle termsSpan = new ClickableSpan() {
        @Override public void updateDrawState(TextPaint ds) {
          super.updateDrawState(ds);
          ds.setColor(getResources().getColor(R.color.white));
          ds.setUnderlineText(false);
        }

        @Override public void onClick(View widget) {
          onClick.onClick(widget);
        }
      };
      spannableBuilder.setSpan(termsSpan, termsStart, termsStart + replaceText.length(),
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
  }

  @Override protected void initializePresenter() {
    if (sessionTokenPreference.get() != null) {
      retrieveOnUpgradeInfo();
    } else {
      if (shouldShowIntro.get()) {
        shouldShowIntro.set(false);
        startActivity(new Intent(this, IntroActivity.class));
        finish();
      } else {
        setupFacebook();
      }
    }
  }

  private void retrieveOnUpgradeInfo() {
    buttonsContainer.setVisibility(View.GONE);
    showLoading();
    final Intent i = new Intent(this, MainTabbedActivity.class);
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    getUserByIdInteractor.loadUserById(currentUserIdPreference.get(), false,
        new Interactor.Callback<User>() {
          @Override public void onLoaded(User user) {
            performAutoLoginInteractor.storePostAutoLoginInfo(user.getIdUser());
            String visibleStreamId = user.getIdWatchingStream();
            if (visibleStreamId != null) {
              getStreamById.loadStream(visibleStreamId, new GetStreamInteractor.Callback() {
                @Override public void onLoaded(Stream stream) {
                  hideLoading();
                  startActivity(i);
                  finish();
                }
              });
            } else {
              hideLoading();
              startActivity(i);
              finish();
            }
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
                /* no-op */
          }
        });
  }

  @Override protected boolean requiresUserLogin() {
    return false;
  }

  private void setupFacebook() {
    FacebookSdk.sdkInitialize(getApplicationContext());
    callbackManager = CallbackManager.Factory.create();
    loginManager = LoginManager.getInstance();

    loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
      @Override public void onSuccess(LoginResult loginResult) {
        final AccessToken accessToken = loginResult.getAccessToken();
        Timber.d("FB Token: %s", accessToken.getToken());
        performFacebookLoginInteractor.attempLogin(accessToken.getToken(),
            new Interactor.Callback<Boolean>() {
              @Override public void onLoaded(Boolean isNewUser) {
                finish();
                Intent intent;
                if (isNewUser) {
                  sendAnalytics();
                  intent = new Intent(LoginSelectionActivity.this, WelcomePageActivity.class);
                } else {
                  intent = new Intent(LoginSelectionActivity.this, MainTabbedActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
              }
            }, new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                showFacebookError(facebookMethodError);
                hideLoading();
              }
            });
      }

      @Override public void onError(FacebookException e) {
        Timber.e(e, "Failed to obtain FB access token");
        showFacebookError(facebookError);
        hideLoading();
      }

      @Override public void onCancel() {
        hideLoading();
      }
    });
  }

  private void sendAnalytics() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionSignup);
    builder.setLabelId(analyticsLabelSignup);
    builder.setUser(sessionRepository.getCurrentUser());
    analyticsTool.analyticsSendAction(builder);
    analyticsTool.appsFlyerSendAction(builder);
  }

  @OnClick(R.id.login_btn_login) public void login() {
    startActivity(new Intent(this, EmailLoginActivity.class));
  }

  @OnClick(R.id.login_btn_email) public void registerWithEmail() {
    startActivity(new Intent(this, EmailRegistrationActivity.class));
  }

  @OnClick(R.id.login_btn_facebook) public void loginWithFacebook() {
    showLoading();
    loginManager.logInWithReadPermissions(this, Arrays.asList(FACEBOOK_PERMISIONS));
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }

  private void showLoading() {
    loading.setVisibility(View.VISIBLE);
    buttonsContainer.setVisibility(View.INVISIBLE);
  }

  private void hideLoading() {
    loading.setVisibility(View.GONE);
    buttonsContainer.setVisibility(View.VISIBLE);
  }

  private void showFacebookError(String errorMessage) {
    feedbackMessage.show(getView(), errorMessage);
  }
}
