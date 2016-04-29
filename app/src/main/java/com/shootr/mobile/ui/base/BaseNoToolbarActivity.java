package com.shootr.mobile.ui.base;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.ViewGroup;
import com.shootr.mobile.ShootrApplication;
import com.shootr.mobile.ui.AppContainer;
import dagger.ObjectGraph;
import javax.inject.Inject;

public class BaseNoToolbarActivity extends ActionBarActivity {

    @Inject AppContainer appContainer;

    private ViewGroup container;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getObjectGraph().inject(this);
        container = appContainer.get(this);
    }

    /**
     * Sets content view manually through {@link AppContainer},
     * with controls the root view for the activity.
     */
    public void setContainerContent(int layoutResID) {
        getLayoutInflater().inflate(layoutResID, container);
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

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
}
