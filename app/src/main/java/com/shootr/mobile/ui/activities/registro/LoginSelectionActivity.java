package com.shootr.mobile.ui.activities.registro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import butterknife.Bind;
import butterknife.BindString;
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
import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.GetStreamInteractor;
import com.shootr.mobile.domain.interactor.user.GetUserByIdInteractor;
import com.shootr.mobile.domain.interactor.user.PerformFacebookLoginInteractor;
import com.shootr.mobile.ui.activities.IntroActivity;
import com.shootr.mobile.ui.activities.MainTabbedActivity;
import com.shootr.mobile.ui.activities.WelcomePageActivity;
import com.shootr.mobile.ui.base.BaseActivity;
import com.shootr.mobile.util.FeedbackMessage;
import java.util.Arrays;
import javax.inject.Inject;
import timber.log.Timber;

public class LoginSelectionActivity extends BaseActivity {

    private static final String[] FACEBOOK_PERMISIONS = { "public_profile", "user_friends", "email" };

    @Bind(R.id.login_progress) View loading;
    @Bind(R.id.login_buttons) View buttonsContainer;
    @BindString(R.string.error_facebook_login) String facebookError;

    @Inject PerformFacebookLoginInteractor performFacebookLoginInteractor;
    @Inject FeedbackMessage feedbackMessage;
    @Inject @SessionToken StringPreference sessionTokenPreference;
    @Inject @CurrentUserId StringPreference currentUserIdPreference;
    @Inject GetUserByIdInteractor getUserByIdInteractor;
    @Inject GetStreamInteractor getStreamById;
    @Inject @ShouldShowIntro BooleanPreference shouldShowIntro;

    private CallbackManager callbackManager;
    private LoginManager loginManager;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_login;
    }

    @Override
    protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    @Override
    protected void initializePresenter() {
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
        final Intent i = new Intent(this, MainTabbedActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getUserByIdInteractor.loadUserById(currentUserIdPreference.get(), new Interactor.Callback<User>() {
            @Override public void onLoaded(User user) {
                String visibleEventId = user.getIdWatchingStream();
                if (visibleEventId != null) {
                    getStreamById.loadStream(visibleEventId, new GetStreamInteractor.Callback() {
                        @Override public void onLoaded(Stream stream) {
                            startActivity(i);
                            finish();
                        }
                    });
                } else {
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

    @Override
    protected boolean requiresUserLogin() {
        return false;
    }

    private void setupFacebook() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();

        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final AccessToken accessToken = loginResult.getAccessToken();
                Timber.d("FB Token: %s", accessToken.getToken());
                performFacebookLoginInteractor.attempLogin(accessToken.getToken(), new Interactor.Callback<Boolean>() {
                    @Override public void onLoaded(Boolean isNewUser) {
                        finish();
                        Intent intent;
                        if (isNewUser) {
                            intent = new Intent(LoginSelectionActivity.this, WelcomePageActivity.class);
                        } else {
                            intent = new Intent(LoginSelectionActivity.this, MainTabbedActivity.class);
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }, new Interactor.ErrorCallback() {
                    @Override
                    public void onError(ShootrException error) {
                        showFacebookError();
                        hideLoading();
                    }
                });
            }

            @Override
            public void onError(FacebookException e) {
                Timber.e(e, "Failed to obtain FB access token");
                showFacebookError();
                hideLoading();
            }

            @Override
            public void onCancel() {
                hideLoading();
            }
        });
    }

    @OnClick(R.id.login_btn_login)
    public void login() {
        startActivity(new Intent(this, EmailLoginActivity.class));
    }

    @OnClick(R.id.login_btn_email)
    public void registerWithEmail() {
        startActivity(new Intent(this, EmailRegistrationActivity.class));
    }

    @OnClick(R.id.login_btn_facebook)
    public void loginWithFacebook() {
        showLoading();
        loginManager.logInWithReadPermissions(this, Arrays.asList(FACEBOOK_PERMISIONS));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    private void showFacebookError() {
        feedbackMessage.show(getView(), facebookError);
    }
}
