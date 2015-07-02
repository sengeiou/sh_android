package com.shootr.android.ui.activities.registro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.shootr.android.R;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.PerformFacebookLoginInteractor;
import com.shootr.android.ui.activities.MainTabbedActivity;
import com.shootr.android.ui.base.BaseActivity;
import com.shootr.android.util.PicassoWrapper;
import hugo.weaving.DebugLog;
import java.util.Arrays;
import javax.inject.Inject;
import timber.log.Timber;

public class LoginSelectionActivity extends BaseActivity {

    private static final String[] FACEBOOK_PERMISIONS = { "public_profile", "user_friends", "email" };

    @Inject PerformFacebookLoginInteractor performFacebookLoginInteractor;

    private CallbackManager callbackManager;
    private LoginManager loginManager;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_login;
    }

    @Override
    protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.inject(this);
    }

    @Override
    protected void initializePresenter() {
        setupFacebook();
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
                performFacebookLoginInteractor.attempLogin(accessToken.getToken(), new Interactor.CompletedCallback() {
                    @Override
                    public void onCompleted() {
                        finish();
                        Intent i = new Intent(LoginSelectionActivity.this, MainTabbedActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                }, new Interactor.ErrorCallback() {
                    @Override
                    public void onError(ShootrException error) {
                        showFacebookError();
                    }
                });
            }

            @Override
            public void onError(FacebookException e) {
                Timber.e(e, "Failed to obtain FB access token");
                showFacebookError();
            }

            @Override
            public void onCancel() {
                /* no-op */
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
        loginManager.logInWithReadPermissions(this, Arrays.asList(FACEBOOK_PERMISIONS));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void showFacebookError() {
        Toast.makeText(LoginSelectionActivity.this, R.string.error_facebook_login, Toast.LENGTH_SHORT).show();
    }
}
