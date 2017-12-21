package com.shootr.mobile.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.ShootrApplication;
import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.data.bus.ServerDown;
import com.shootr.mobile.data.bus.Unauthorized;
import com.shootr.mobile.data.bus.VersionOutdatedError;
import com.shootr.mobile.domain.service.SessionHandler;
import com.shootr.mobile.ui.AppContainer;
import com.shootr.mobile.ui.activities.UpdateWarningActivity;
import com.shootr.mobile.ui.activities.WhaleActivity;
import com.shootr.mobile.ui.activities.registro.LoginSelectionActivity;
import com.shootr.mobile.util.VersionUpdater;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import dagger.ObjectGraph;
import javax.inject.Inject;

public abstract class BaseActivity extends ActionBarActivity {

    @Inject AppContainer appContainer;
    @Inject @Main Bus bus;
    @Inject VersionUpdater versionUpdater;
    @Inject SessionHandler sessionHandler;

    private ServerDown.Receiver serverDownReceiver;
    private VersionOutdatedError.Receiver preconditionFailedReceiver;
    private Unauthorized.Receiver unauthorizedReceiver;
    private ObjectGraph activityGraph;
    private View activityView;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
        setupWhalePage();
        setupUpdateWarningPage();
        setupUnauthorizedRedirection();
        if (!requiresUserLogin() || sessionHandler.hasSession()) {
            createLayout();
            initializeViews(savedInstanceState);
            initializePresenter();
        } else {
            redirectToLogin();
        }
    }

    protected boolean requiresUserLogin() {
        return true;
    }

    protected void injectDependencies() {
        getObjectGraph().inject(this);
    }

    protected void createLayout() {
        setContent(getLayoutResource());
    }

    @LayoutRes protected abstract int getLayoutResource();

    protected abstract void initializeViews(Bundle savedInstanceState);

    protected abstract void initializePresenter();

    @Override protected void onResume() {
        super.onResume();
        bus.register(serverDownReceiver);
        bus.register(unauthorizedReceiver);
        bus.register(preconditionFailedReceiver);
    }

    @Override protected void onPause() {
        super.onPause();
        try {
            bus.unregister(serverDownReceiver);
            bus.unregister(unauthorizedReceiver);
            bus.unregister(preconditionFailedReceiver);
        } catch (RuntimeException error) {
            /* no-op */
        }
    }

    @Override protected void onDestroy() {
        activityGraph = null;
        super.onDestroy();
    }

    public void inject(Object object) {
        getObjectGraph().inject(object);
    }

    public ObjectGraph getObjectGraph() {
        if (activityGraph == null) {
            activityGraph = buildObjectGraph();
        }
        return activityGraph;
    }

    protected ObjectGraph buildObjectGraph() {
        return ShootrApplication.get(this).getObjectGraph();
    }

    protected void setContent(@LayoutRes int layoutResource) {
        activityView = getLayoutInflater().inflate(layoutResource, getContentViewRoot());
    }

    protected ViewGroup getContentViewRoot() {
        return appContainer.get(this);
    }

    private void setupWhalePage() {
        serverDownReceiver = new ServerDown.Receiver() {
            @Subscribe @Override public void onServerDown(ServerDown.Event event) {
                openWhalePage();
            }
        };
    }

    private void setupUpdateWarningPage() {
        preconditionFailedReceiver = new VersionOutdatedError.Receiver() {
            @Subscribe @Override public void onVersionOutdatedError(VersionOutdatedError.Event event) {
                openUpdateWarningPage();
            }
        };
    }

    private void setupUnauthorizedRedirection() {
        unauthorizedReceiver = new Unauthorized.Receiver() {
            @Subscribe @Override public void onUnauthorized(Unauthorized.Event event) {
                if (requiresUserLogin()) {
                    redirectToLogin();
                }
            }
        };
    }

    private void openWhalePage() {
        startActivity(WhaleActivity.newIntent(this));
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    private void openUpdateWarningPage() {
        startActivity(UpdateWarningActivity.newIntent(this));
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    protected void redirectToLogin() {
        startActivity(new Intent(this, LoginSelectionActivity.class));
        finish();
    }

    public View getView() {
        return activityView;
    }

    public SessionHandler getSessionHandler() {
        return sessionHandler;
    }
}
