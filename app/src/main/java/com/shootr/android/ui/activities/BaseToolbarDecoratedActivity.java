package com.shootr.android.ui.activities;

import android.support.annotation.LayoutRes;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.ViewContainerDecorator;
import com.shootr.android.ui.base.BaseDecoratedActivity;
import com.shootr.android.ui.dagger.ToolbarDecoratedModule;
import com.shootr.android.util.ImageLoader;
import dagger.ObjectGraph;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public abstract class BaseToolbarDecoratedActivity extends BaseDecoratedActivity {

    private ToolbarDecorator toolbarDecorator;
    @Inject ImageLoader imageLoader;

    @Override protected void setContent(@LayoutRes int layoutResource) {
        super.setContent(layoutResource);
        setupDecorators();
        setupToolbar(toolbarDecorator);
    }

    @Override protected List<ViewContainerDecorator> getDecorators() {
        toolbarDecorator = new ToolbarDecorator(this, imageLoader);
        return Collections.singletonList((ViewContainerDecorator) toolbarDecorator);
    }

    @Override
    public ObjectGraph getObjectGraph() {
        return super.getObjectGraph().plus(new ToolbarDecoratedModule(this));
    }

    protected abstract void setupToolbar(ToolbarDecorator toolbarDecorator);

    private void setupDecorators() {
        toolbarDecorator.bindActionbar(this);
    }

    public ToolbarDecorator getToolbarDecorator() {
        return toolbarDecorator;
    }
}
