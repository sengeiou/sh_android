package com.shootr.android.ui.activities.registro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.shootr.android.R;
import com.shootr.android.data.prefs.BooleanPreference;
import com.shootr.android.data.prefs.ShouldShowIntro;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.PerformFacebookLoginInteractor;
import com.shootr.android.ui.activities.IntroActivity;
import com.shootr.android.ui.activities.MainTabbedActivity;
import com.shootr.android.ui.activities.WelcomePageActivity;
import com.shootr.android.ui.base.BaseActivity;
import java.util.Arrays;
import javax.inject.Inject;
import timber.log.Timber;

public class LoginSelectionActivity extends BaseActivity {

    private static final String[] FACEBOOK_PERMISIONS = { "public_profile", "user_friends", "email" };

    @Bind(R.id.login_progress) View loading;
    @Bind(R.id.login_buttons) View buttonsContainer;

    @Inject PerformFacebookLoginInteractor performFacebookLoginInteractor;
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
        if (shouldShowIntro.get()) {
            shouldShowIntro.set(false);
            startActivity(new Intent(this, IntroActivity.class));
            finish();
        } else {
            setupFacebook();
        }
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
                        if (isNewUser) {
                            Intent i = new Intent(LoginSelectionActivity.this, WelcomePageActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else {
                            Intent i = new Intent(LoginSelectionActivity.this, MainTabbedActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }
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
        Toast.makeText(LoginSelectionActivity.this, R.string.error_facebook_login, Toast.LENGTH_SHORT).show();
    }
}
