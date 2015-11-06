package com.shootr.mobile.ui.base;

import android.view.ViewGroup;
import com.shootr.mobile.ui.ViewContainerDecorator;
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
