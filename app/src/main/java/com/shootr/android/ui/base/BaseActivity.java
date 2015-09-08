package com.shootr.android.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.android.R;
import com.shootr.android.ShootrApplication;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.bus.ServerDown;
import com.shootr.android.data.bus.Unauthorized;
import com.shootr.android.data.bus.VersionOutdatedError;
import com.shootr.android.domain.service.SessionHandler;
import com.shootr.android.ui.AppContainer;
import com.shootr.android.ui.activities.UpdateWarningActivity;
import com.shootr.android.ui.activities.WhaleActivity;
import com.shootr.android.ui.activities.registro.LoginSelectionActivity;
import com.shootr.android.util.VersionUpdater;
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

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
        setupWhalePage();
        setupUpdateWarningPage();
        setupUnauthorizedRedirection();
        if (!requiresUserLogin() || sessionHandler.hasSession()) {
            createLayout();
            initializeViews(savedInstanceState);
        } else {
            redirectToLogin();
        }
    }

    protected boolean requiresUserLogin() {
        return true;
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initializePresenter();
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
        versionUpdater.checkVersionCompatible();
    }

    @Override protected void onPause() {
        super.onPause();
        bus.unregister(serverDownReceiver);
        bus.unregister(unauthorizedReceiver);
        bus.unregister(preconditionFailedReceiver);
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
            @Subscribe
            @Override
            public void onUnauthorized(Unauthorized.Event event) {
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

    private void redirectToLogin() {
        startActivity(new Intent(this, LoginSelectionActivity.class));
        finish();
    }

    public View getView() {
        return activityView;
    }
}
