package com.shootr.android.ui.activities;

import android.support.annotation.LayoutRes;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.ViewContainerDecorator;
import com.shootr.android.ui.base.BaseDecoratedActivity;
import com.shootr.android.util.PicassoWrapper;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public abstract class BaseToolbarDecoratedActivity extends BaseDecoratedActivity {

    private ToolbarDecorator toolbarDecorator;
    @Inject PicassoWrapper picasso;

    @Override protected void setContent(@LayoutRes int layoutResource) {
        super.setContent(layoutResource);
        setupDecorators();
        setupToolbar(toolbarDecorator);
    }

    @Override protected List<ViewContainerDecorator> getDecorators() {
        toolbarDecorator = new ToolbarDecorator(this, picasso);
        return Collections.singletonList((ViewContainerDecorator) toolbarDecorator);
    }

    protected abstract void setupToolbar(ToolbarDecorator toolbarDecorator);

    private void setupDecorators() {
        toolbarDecorator.bindActionbar(this);
    }

    public ToolbarDecorator getToolbarDecorator() {
        return toolbarDecorator;
    }
}
