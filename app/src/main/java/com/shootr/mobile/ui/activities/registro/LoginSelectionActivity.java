package com.shootr.mobile.ui.activities.registro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.RelativeLayout;
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
import com.shootr.mobile.data.prefs.ActivityShowcase;
import com.shootr.mobile.data.prefs.BooleanPreference;
import com.shootr.mobile.data.prefs.CurrentUserId;
import com.shootr.mobile.data.prefs.SessionToken;
import com.shootr.mobile.data.prefs.ShouldShowIntro;
import com.shootr.mobile.data.prefs.StringPreference;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.GetStreamInteractor;
import com.shootr.mobile.domain.interactor.user.GetUserByIdInteractor;
import com.shootr.mobile.domain.interactor.user.PerformFacebookLoginInteractor;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.utils.LocaleProvider;
import com.shootr.mobile.ui.activities.MainTabbedActivity;
import com.shootr.mobile.ui.activities.OnBoardingStreamActivity;
import com.shootr.mobile.ui.base.BaseActivity;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.IntentFactory;
import com.shootr.mobile.util.Intents;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import timber.log.Timber;

public class LoginSelectionActivity extends BaseActivity {

  private static final String[] FACEBOOK_PERMISIONS = { "public_profile", "user_friends", "email" };

  @BindView(R.id.login_progress) View loading;
  @BindView(R.id.login_buttons) View buttonsContainer;
  @BindView(R.id.login_selection_legal_disclaimer) TextView disclaimer;
  @BindView(R.id.container) RelativeLayout container;

  @BindString(R.string.error_facebook_login) String facebookError;
  @BindString(R.string.error_login_facebook_method) String facebookMethodError;
  @BindString(R.string.terms_of_service_base_url) String termsOfServiceBaseUrl;
  @BindString(R.string.privacy_policy_service_base_url) String privacyPolicyServiceBaseUrl;
  @BindString(R.string.analytics_action_signup) String analyticsActionSignup;
  @BindString(R.string.analytics_action_open_app) String analyticsActionOpenApp;

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
  @Inject @ActivityShowcase BooleanPreference activityShowcase;
  @Inject ErrorMessageFactory errorMessageFactory;

  private CallbackManager callbackManager;
  private LoginManager loginManager;

  @Override protected int getLayoutResource() {
    return R.layout.activity_login;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    setupDisclaimerLinks();
    setupStatusBarColor();
    setupBackground();
  }

  private void setupBackground() {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
      AnimationDrawable animationDrawable = (AnimationDrawable) container.getBackground();
      animationDrawable.setEnterFadeDuration(2000);
      animationDrawable.setExitFadeDuration(4000);
      animationDrawable.start();
    }
  }

  private void setupStatusBarColor() {
    View decorView = getWindow().getDecorView();
    int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      decorView.setSystemUiVisibility(uiOptions);
    }
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.hide();
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
        termsOfServiceClickListener();
      }
    };
    replacePatternWithClickableText(spannableStringBuilder, termsPatternText, termsText,
        termsClickListener);

    String privacyPatternText = "\\(privacy-policy\\)";
    String privacyText = getString(R.string.activity_registration_legal_disclaimer_privacy_policy);
    final View.OnClickListener privacyClickListener = new View.OnClickListener() {
      @Override public void onClick(View v) {
        privacyPolicyClickListener();
      }
    };
    replacePatternWithClickableText(spannableStringBuilder, privacyPatternText, privacyText,
        privacyClickListener);

    disclaimer.setText(spannableStringBuilder);
    disclaimer.setMovementMethod(new LinkMovementMethod());
  }

  @NonNull public void privacyPolicyClickListener() {
    String privacyUrl = String.format(privacyPolicyServiceBaseUrl, localeProvider.getLanguage());
    Intent privacyIntent =
        intentFactory.openEmbededUrlIntent(LoginSelectionActivity.this, privacyUrl);
    Intents.maybeStartActivity(LoginSelectionActivity.this, privacyIntent);
  }

  @NonNull public void termsOfServiceClickListener() {
    String termsUrl = String.format(termsOfServiceBaseUrl, localeProvider.getLanguage());
    Intent termsIntent = intentFactory.openEmbededUrlIntent(LoginSelectionActivity.this, termsUrl);
    Intents.maybeStartActivity(LoginSelectionActivity.this, termsIntent);
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
        sendOpenAppFirstTimeAnalytics();
        setupIntro();
      } else {
        setupFacebook();
      }
    }
  }

  private void sendOpenAppFirstTimeAnalytics() {
    analyticsTool.sendOpenAppMixPanelAnalytics(analyticsActionOpenApp, getBaseContext());
  }

  private void setupIntro() {
    setupFacebook();
  }

  private void retrieveOnUpgradeInfo() {
    buttonsContainer.setVisibility(View.GONE);
    showLoading();
    final Intent i = new Intent(this, MainTabbedActivity.class);
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    getUserByIdInteractor.loadUserById(currentUserIdPreference.get(), false,
        new Interactor.Callback<User>() {
          @Override public void onLoaded(User user) {
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
        if (!checkFacebookPermissions(loginResult.getRecentlyGrantedPermissions())) {
          feedbackMessage.showLong(getView(), getString(R.string.facebook_permissions_alert));
          final Handler handler = new Handler();
          handler.postDelayed(new Runnable() {
            @Override public void run() {
              final Intent i = new Intent(getBaseContext(), LoginSelectionActivity.class);
              i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
              startActivity(i);
            }
          }, 2500);
        } else {
          performFacebookLoginInteractor.attempLogin(accessToken.getToken(),
              new Interactor.Callback<Boolean>() {
                @Override public void onLoaded(Boolean isNewUser) {
                  finish();
                  Intent intent;
                  if (isNewUser) {
                    sendSignUpAnalythics();
                    activityShowcase.set(true);
                    intent =
                        new Intent(LoginSelectionActivity.this, OnBoardingStreamActivity.class);
                  } else {
                    sendLoginToMixpanel();
                    intent = new Intent(LoginSelectionActivity.this, MainTabbedActivity.class);
                  }
                  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                  startActivity(intent);
                }
              }, new Interactor.ErrorCallback() {
                @Override public void onError(ShootrException error) {
                  String errorMessage = errorMessageFactory.getMessageForError(error);
                  if (errorMessage != null) {
                    showMassiveRegisterError(errorMessage);
                  } else {
                    showFacebookError(facebookMethodError);
                  }
                  hideLoading();
                }
              });
        }
      }

      private void sendLoginToMixpanel() {
        AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
        builder.setContext(getBaseContext());
        builder.setActionId("ConnectionType");
        builder.setLabelId("ConnectionType");
        builder.setSource("FacebookLogin");
        builder.setUser(sessionRepository.getCurrentUser());
        analyticsTool.analyticsSendAction(builder);
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

  private boolean checkFacebookPermissions(Set<String> facebookPermissions) {
    for (int i = 0; i < 3; i++) {
      if (!facebookPermissions.contains(FACEBOOK_PERMISIONS[i])) {
        return false;
      }
    }
    return true;
  }

  private void sendSignUpAnalythics() {
    analyticsTool.sendSignUpEvent(sessionRepository.getCurrentUser(), analyticsActionSignup,
        getBaseContext());
  }

  @OnClick(R.id.login_btn_login) public void login() {
    startActivity(new Intent(this, EmailLoginActivity.class));
  }

  @OnClick(R.id.login_btn_email) public void registerWithEmail() {
    Intent intent = new Intent(this, EmailRegistrationActivity.class);
    startActivity(intent);
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

  private void showMassiveRegisterError(String errorMessage) {
    new AlertDialog.Builder(this).setMessage(errorMessage)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            /* no-op */
          }
        })
        .show();
  }

  @Override protected void onResume() {
    super.onResume();
    setupStatusBarColor();
  }
}
