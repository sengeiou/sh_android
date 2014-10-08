package gm.mobi.android.ui.base;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewGroup;
import dagger.ObjectGraph;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.ui.AppContainer;
import javax.inject.Inject;

public class BaseActivity extends ActionBarActivity {

    @Inject AppContainer appContainer;

    private ViewGroup container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        return GolesApplication.get(this).getObjectGraph();
    }
}
