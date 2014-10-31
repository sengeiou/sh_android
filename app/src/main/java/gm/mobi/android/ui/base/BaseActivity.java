package gm.mobi.android.ui.base;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import com.google.android.gms.fitness.request.ac;
import javax.inject.Inject;

import dagger.ObjectGraph;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.ui.AppContainer;

import static butterknife.ButterKnife.findById;

public class BaseActivity extends ActionBarActivity {

    @Inject AppContainer appContainer;

    private ViewGroup container;

    private Toolbar actionBarToolbar;

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
        return GolesApplication.get(this).getObjectGraph();
    }

    public Toolbar getToolbar(){ return actionBarToolbar;}

    protected Toolbar getActionBarToolbar(){
        if(actionBarToolbar == null){
            actionBarToolbar = (Toolbar)findViewById(R.id.toolbar_actionbar);
            if(actionBarToolbar!=null){
                setSupportActionBar(actionBarToolbar);
            }
        }
        return actionBarToolbar;
    }
}
