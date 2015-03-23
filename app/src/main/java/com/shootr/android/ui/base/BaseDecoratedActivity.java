package com.shootr.android.ui.base;

import android.view.ViewGroup;
import com.shootr.android.ui.ViewContainerDecorator;
import java.util.List;

public abstract class BaseDecoratedActivity extends BaseActivity {

    private ViewGroup decoratedContent;

    @Override protected ViewGroup getContentViewRoot() {
        if (decoratedContent == null) {
            decoratedContent = super.getContentViewRoot();
            for (ViewContainerDecorator viewDecorator : getDecorators()) {
                decoratedContent = viewDecorator.decorateContainer(decoratedContent);
            }
        }
        return decoratedContent;
    }

    protected abstract List<ViewContainerDecorator> getDecorators();
}
