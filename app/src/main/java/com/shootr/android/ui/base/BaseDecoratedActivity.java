package com.shootr.android.ui.base;

import android.view.ViewGroup;
import com.shootr.android.ui.ViewContainerDecorator;
import java.util.List;

public abstract class BaseDecoratedActivity extends BaseActivity{

    @Override protected ViewGroup getContentViewRoot() {
        ViewGroup activityRoot = super.getContentViewRoot();
        for (ViewContainerDecorator viewDecorator : getDecorators()) {
            activityRoot = viewDecorator.decorateContainer(activityRoot);
        }
        return activityRoot;
    }

    protected abstract List<ViewContainerDecorator> getDecorators();
}
