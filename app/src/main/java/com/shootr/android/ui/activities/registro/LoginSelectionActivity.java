package com.shootr.android.ui.activities.registro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseActivity;
import com.shootr.android.util.PicassoWrapper;
import hugo.weaving.DebugLog;
import java.util.Arrays;
import javax.inject.Inject;
import timber.log.Timber;

public class LoginSelectionActivity extends BaseActivity {

    private CallbackManager callbackManager;
    private LoginManager loginManager;

    @InjectView(R.id.login_icon) ImageView loginIcon;

    @Inject PicassoWrapper picasso;

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
            @DebugLog
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Timber.d("Facebook Access Token: %s", accessToken.getToken());
                Profile profile = Profile.getCurrentProfile();
                String name = profile.getName();
                new AlertDialog.Builder(LoginSelectionActivity.this).setTitle("Hello " + name)
                  .setMessage(String.format(
                    "Welcome to Shootr and stuff.\n\nYour name is %s and your access token is something like %s...",
                    name,
                    accessToken.getToken().substring(0, 20)))
                  .setPositiveButton("Ok", null)
                  .show();
                picasso.load(profile.getProfilePictureUri(200, 200)).into(loginIcon);
            }

            @Override
            public void onCancel() {
                /* no-op */
            }

            @Override
            @DebugLog
            public void onError(FacebookException e) {
                //TODO handle error
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
        loginManager.logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
