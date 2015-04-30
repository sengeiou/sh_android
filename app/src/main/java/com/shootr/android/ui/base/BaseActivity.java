package com.shootr.android.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewGroup;
import com.shootr.android.ShootrApplication;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.bus.UpdateWarning;
import com.shootr.android.domain.service.SessionHandler;
import com.shootr.android.ui.AppContainer;
import com.shootr.android.ui.activities.UpdateWarningActivity;
import com.shootr.android.ui.activities.registro.WelcomeLoginActivity;
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

    private UpdateWarning.Receiver updateWarningReceiver;
    private ObjectGraph activityGraph;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
        setupUpdateWarning();
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
        activityGraph = getObjectGraph();
        activityGraph.inject(this);
    }

    protected void createLayout() {
        setContent(getLayoutResource());
    }

    @LayoutRes protected abstract int getLayoutResource();

    protected abstract void initializeViews(Bundle savedInstanceState);

    protected abstract void initializePresenter();

    @Override protected void onResume() {
        super.onResume();
        bus.register(updateWarningReceiver);
    }

    @Override protected void onPause() {
        super.onPause();
        bus.unregister(updateWarningReceiver);
    }

    @Override protected void onDestroy() {
        activityGraph = null;
        super.onDestroy();
    }

    public void inject(Object object) {
        activityGraph.inject(object);
    }

    protected ObjectGraph getObjectGraph() {
        return ShootrApplication.get(this).getObjectGraph();
    }

    protected void setContent(@LayoutRes int layoutResource) {
        getLayoutInflater().inflate(layoutResource, getContentViewRoot());
    }

    protected ViewGroup getContentViewRoot() {
        return appContainer.get(this);
    }

    private void setupUpdateWarning() {
        updateWarningReceiver = new UpdateWarning.Receiver() {
            @Subscribe @Override public void onUpdateWarning(UpdateWarning.Event event) {
                openUpdateWarning();
            }
        };
        bus.register(updateWarningReceiver);
        versionUpdater.checkVersionCompatible();
    }

    public void openUpdateWarning() {
        startActivity(new Intent(this, UpdateWarningActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void redirectToLogin() {
        startActivity(new Intent(this, WelcomeLoginActivity.class));
        finish();
    }
}
