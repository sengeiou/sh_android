package com.shootr.android.ui.activities;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import com.shootr.android.R;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.NavigationDrawerDecorator;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.ViewContainerDecorator;
import com.shootr.android.ui.base.BaseDecoratedActivity;
import com.shootr.android.util.PicassoWrapper;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

public abstract class BaseNavDrawerToolbarActivity extends BaseDecoratedActivity {

    private NavigationDrawerDecorator navigationDrawerDecorator;
    private ToolbarDecorator toolbarDecorator;

    @Inject SessionRepository sessionRepository;
    @Inject PicassoWrapper picasso;

    @Override protected void setContent(@LayoutRes int layoutResource) {
        super.setContent(layoutResource);
        setupDecorators();
        overridePendingTransition(0, 0);
        navigationDrawerDecorator.fadeInContent();
    }

    @Override protected List<ViewContainerDecorator> getDecorators() {
        navigationDrawerDecorator = new NavigationDrawerDecorator(this, picasso, getNavDrawerItemId());
        toolbarDecorator = new ToolbarDecorator(this);
        return Arrays.asList(navigationDrawerDecorator, toolbarDecorator);
    }

    protected abstract int getNavDrawerItemId();

    private void setupDecorators() {
        toolbarDecorator.bindActionbar(this);
        toolbarDecorator.setTitle(R.string.drawer_timeline_title);

        navigationDrawerDecorator.bindToolbar(toolbarDecorator.getToolbar());
        navigationDrawerDecorator.setNavDrawerItemClickListener(new NavigationDrawerDecorator.OnNavDrawerItemClickedListener() {
            @Override public void goToNavDrawerItem(int itemId) {
                Intent intent = null;
                switch (itemId) {
                    case NavigationDrawerDecorator.NAVDRAWER_ITEM_TIMELINE:
                        intent = new Intent(BaseNavDrawerToolbarActivity.this, TimelineActivity.class);
                        break;
                    case NavigationDrawerDecorator.NAVDRAWER_ITEM_PEOPLE:
                        intent = new Intent(BaseNavDrawerToolbarActivity.this, PeopleActivity.class);
                        break;
                }
                startActivity(intent);
                finish();
            }
        });
        navigationDrawerDecorator.bindUser(sessionRepository.getCurrentUser());
    }

    public ToolbarDecorator getToolbarDecorator() {
        return toolbarDecorator;
    }
}
