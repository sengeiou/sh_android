package gm.mobi.android.ui.activities.registro;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.task.BusProvider;
import gm.mobi.android.task.events.FacebookProfileEvent;
import gm.mobi.android.task.jobs.GetFacebookProfileJob;
import gm.mobi.android.ui.activities.PartidoActivity;
import gm.mobi.android.ui.adapters.WelcomePagerAdapter;
import gm.mobi.android.ui.base.BaseActivity;
import gm.mobi.android.ui.widgets.WelcomeIndicator;
import gm.mobi.android.util.FacebookUtils;
import gm.mobi.android.util.LogUtils;

public class WelcomeLoginActivity extends BaseActivity {

    // Main layouts
    @InjectView(R.id.welcome_container) View mWelcomeContainer;
    @InjectView(R.id.login_container) View mLoginContainer;

    // Welcome views
    @InjectView(R.id.pager) ViewPager mPager;
    @InjectView(R.id.welcome_icon) ImageView mWelcomeIcon;
    @InjectView(R.id.welcome_button) Button mWelcomeButton;
    @InjectView(R.id.welcome_arrow_left) View mArrowLeft;
    @InjectView(R.id.welcome_arrow_right) View mArrowRight;
    @InjectView(R.id.welcome_indicators) WelcomeIndicator mIndicator;

    // Login views
    @InjectView(R.id.login_icon) ImageView mLoginIcon;
    @InjectView(R.id.login_btn_facebook) TextView mTitle;
    @InjectView(R.id.login_subtitle) TextView mSubtitle;
    @InjectView(R.id.login_btn_facebook) LoginButton mButtonFacebook;
    @InjectView(R.id.login_btn_email) Button mButtonEmail;
    @InjectView(R.id.login_btn_login) TextView mButtonLogin;
    @InjectView(R.id.login_skip) TextView mButtonSkip;


    private Context mContext = this;
    private WelcomePagerAdapter mAdapter;
    private UiLifecycleHelper uiHelper;
    private Bus bus;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_login);
        ButterKnife.inject(this);
        bus = BusProvider.getInstance();

        setupWelcomePage();
        setupLoginPage(savedInstanceState);
    }


     /* --- UI methods --- */


    private void setupWelcomePage() {
        mAdapter = new WelcomePagerAdapter(this);
        mIndicator.setItemCount(mAdapter.getCount());
        mIndicator.setActiveItem(0);
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                View page = mAdapter.getItem(position);
                Integer iconRes = (Integer) page.getTag();
                mWelcomeIcon.setImageResource(iconRes);
                mIndicator.setActiveItem(position);

                // Show and hide arrows if necessary
                if (mAdapter.isFirst(position)) {
                    mArrowLeft.setVisibility(View.INVISIBLE);
                } else {
                    mArrowLeft.setVisibility(View.VISIBLE);
                }

                if (mAdapter.isLast(position)) {
                    mArrowRight.setVisibility(View.INVISIBLE);
                } else {
                    mArrowRight.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
        });

        mWelcomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePageToLogin();
            }
        });

        mArrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
            }
        });

        mArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1, true);
            }
        });
    }

    private void setupLoginPage(Bundle savedInstanceState) {
        mButtonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, EmailRegistrationActivity.class));
            }
        });
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, EmailLoginActivity.class));
            }
        });
        mButtonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(PartidoActivity.getIntent(WelcomeLoginActivity.this));
            }
        });

        // Mata sesión de facebook anterior si había
        Session activeSession = Session.getActiveSession();
        if (!(activeSession == null || activeSession.getState().isClosed())) {
            LogUtils.d("Matando sesión actual");
            activeSession.closeAndClearTokenInformation();
        }

        // Facebook setup
        mButtonFacebook.setReadPermissions(Arrays.asList("public_profile", "email"));
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
    }

    private void changePageToLogin() {
        hideWelcomePage();
        showLoginPage();
    }

    private void hideWelcomePage() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        List<Animator> animations = new ArrayList<Animator>(6);
        animations.add(ObjectAnimator.ofFloat(mWelcomeIcon, "translationY", -height).setDuration(250));
        animations.add(ObjectAnimator.ofFloat(mPager, "translationY", -height).setDuration(250));
        animations.add(ObjectAnimator.ofFloat(mWelcomeButton, "translationY", height).setDuration(250));
        animations.add(ObjectAnimator.ofFloat(mArrowRight, "alpha", 0f).setDuration(100));
        animations.add(ObjectAnimator.ofFloat(mArrowLeft, "alpha", 0f).setDuration(100));
        animations.add(ObjectAnimator.ofFloat(mIndicator, "alpha", 0f).setDuration(100));

        AnimatorSet set = new AnimatorSet();
        set.playTogether(animations);
        set.setInterpolator(new AccelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mWelcomeContainer.setVisibility(View.GONE);
            }
        });
        set.start();
    }

    public void showLoginPage() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mLoginContainer, "scaleX", 0.25f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mLoginContainer, "scaleY", 0.25f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mLoginContainer, "alpha", 0f, 0.2f, 1f);
        set.playTogether(scaleX, scaleY, alpha);
        set.setInterpolator(new DecelerateInterpolator());
        set.setDuration(300);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mLoginContainer.setVisibility(View.VISIBLE);
            }
        });
        set.start();
    }


    /* --- Simple Logic methods --- */

    /**
     * Detecta el login de facebook
     */
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        String accessToken = session.getAccessToken();
        if (state.isOpened() && accessToken != null && !TextUtils.isEmpty(accessToken)) {
            LogUtils.d("Logged in");
            LogUtils.d("Access Token: " + accessToken);
            Toast.makeText(this, "Obteniendo datos", Toast.LENGTH_SHORT).show();
            mButtonFacebook.setEnabled(false);
            /* Launch background job, result received at {@link #fbProfileReceived(FacebookProfileEvent)} */
            GolesApplication.getInstance().getJobManager().addJobInBackground(new GetFacebookProfileJob(session));
        } else if (state.isClosed()) {
            LogUtils.d("Logged out or activity recreated");
            mButtonFacebook.setEnabled(true);
        }
    }

    @Subscribe
    public void fbProfileReceived(FacebookProfileEvent event) {
        if (!event.hasError()) {
            // Launch facebook registration activity with fb data
            GraphUser graphUser = event.getGraphUser();
            String email = (String) graphUser.getProperty("email");
            String username = email.substring(0, email.indexOf("@")).replace(".", "");
            String avatar = FacebookUtils.getAvatarUrl(graphUser.getId());
            // We need to destroy de current activity, because coming back will display Logout in facebook button
            startActivity(FacebookRegistroActivity.getIntent(this, email, username, avatar).addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish(); // Clear task doesn't work on api<11
            String message = "Bienvenido, " + graphUser.getFirstName();
            LogUtils.d(message);

        } else {
            String message = "Error al obtener el perfil: " + event.getError().getMessage();
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            LogUtils.e(message, event.getError());
            //TODO error handling bueno
        }

    }


    /* Activity lifecycle stuff */
    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        bus.register(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        bus.unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
}
