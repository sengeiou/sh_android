package com.shootr.mobile.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.ShootrApplication;
import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.data.bus.UpdateWarning;
import com.shootr.mobile.ui.AppContainer;
import com.shootr.mobile.ui.activities.UpdateWarningActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import dagger.ObjectGraph;
import javax.inject.Inject;

import static butterknife.ButterKnife.findById;

public class BaseToolbarActivity extends ActionBarActivity {

    @Inject AppContainer appContainer;
    @Inject @Main Bus bus;

    private ViewGroup container;

    private Toolbar actionBarToolbar;
    private UpdateWarning.Receiver updateWarningReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getObjectGraph().inject(this);
        container = appContainer.get(this);

        updateWarningReceiver = new UpdateWarning.Receiver() {
            @Subscribe @Override public void onUpdateWarning(UpdateWarning.Event event) {
                openUpdateWarning();
            }
        };
        bus.register(updateWarningReceiver);
    }

    /**
     * Sets content view manually through {@link AppContainer},
     * with controls the root view for the activity.
     */
    public void setContainerContent(int layoutResID) {
        ViewGroup actionBarDecor = (ViewGroup) getLayoutInflater().inflate(R.layout.action_bar_decor, container);
        getActionBarToolbar();
        ViewGroup activityContent = findById(actionBarDecor, R.id.action_bar_activity_content);
        getLayoutInflater().inflate(layoutResID, activityContent);
    }


    /**
     * Base implementation for the Activity object graph.
     * Can be extended to provide different or extended graphs to inject from.
     *
     * @return By default returns the Application global object graph.
     */
    public ObjectGraph getObjectGraph() {
        return ShootrApplication.get(this).getObjectGraph();
    }

    public Toolbar getToolbar(){
        return actionBarToolbar;
    }

    protected Toolbar getActionBarToolbar(){
        if(actionBarToolbar == null){
            actionBarToolbar = (Toolbar)findViewById(R.id.toolbar_actionbar);
            if(actionBarToolbar!=null){
                setSupportActionBar(actionBarToolbar);
            }
        }
        return actionBarToolbar;
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override protected void onResume() {
        super.onResume();
        bus.register(updateWarningReceiver);
    }

    @Override protected void onPause() {
        super.onPause();
        bus.unregister(updateWarningReceiver);
    }

    public void openUpdateWarning() {
        startActivity(new Intent(this, UpdateWarningActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
