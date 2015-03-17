package com.shootr.android.ui.activities;

import android.support.annotation.LayoutRes;
import com.shootr.android.R;
import com.shootr.android.ui.NavigationDrawerDecorator;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.ViewContainerDecorator;
import com.shootr.android.ui.base.BaseDecoratedActivity;
import java.util.Arrays;
import java.util.List;

public abstract class BaseNavDrawerToolbarActivity extends BaseDecoratedActivity {

    private NavigationDrawerDecorator navigationDrawerDecorator;

    private ToolbarDecorator toolbarDecorator;

    @Override protected void setContent(@LayoutRes int layoutResource) {
        super.setContent(layoutResource);
        setupDecorators();
    }

    @Override protected List<ViewContainerDecorator> getDecorators() {
        navigationDrawerDecorator = new NavigationDrawerDecorator(this, getNavDrawerItemId());
        toolbarDecorator = new ToolbarDecorator(this);
        return Arrays.asList(navigationDrawerDecorator, toolbarDecorator);
    }

    protected abstract int getNavDrawerItemId();

    private void setupDecorators() {
        toolbarDecorator.bindActionbar(this);
        toolbarDecorator.setTitle(R.string.drawer_timeline_title);

        navigationDrawerDecorator.bindToolbar(toolbarDecorator.getToolbar());
    }

    public ToolbarDecorator getToolbarDecorator() {
        return toolbarDecorator;
    }
}
