package gm.mobi.android.ui.base;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewGroup;

import javax.inject.Inject;

import gm.mobi.android.GolesApplication;
import gm.mobi.android.ui.AppContainer;

public class BaseActivity extends ActionBarActivity {

    @Inject AppContainer appContainer;

    private ViewGroup container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GolesApplication app = GolesApplication.get(this);
        app.inject(this);
        container = appContainer.get(this);
    }

    /**
     * Sets content view manually through {@link AppContainer},
     * with controls the root view for the activity.
     */
    public void setContainerContent(int layoutResID) {
        getLayoutInflater().inflate(layoutResID, container);
    }
}
