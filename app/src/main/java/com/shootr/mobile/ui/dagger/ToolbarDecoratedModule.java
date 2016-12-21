package com.shootr.mobile.ui.dagger;

import com.shootr.mobile.ShootrModule;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.activities.BaseToolbarDecoratedActivity;
import com.shootr.mobile.ui.fragments.PrivateMessageTimelineFragment;
import com.shootr.mobile.ui.fragments.StreamTimelineFragment;
import com.shootr.mobile.ui.fragments.StreamsListFragment;
import dagger.Module;
import dagger.Provides;

@Module(
  injects = {
    StreamsListFragment.class, StreamTimelineFragment.class, PrivateMessageTimelineFragment.class
  },
  addsTo = ShootrModule.class) public class ToolbarDecoratedModule {

    private final BaseToolbarDecoratedActivity baseToolbarDecoratedActivity;

    public ToolbarDecoratedModule(BaseToolbarDecoratedActivity baseToolbarDecoratedActivity) {
        this.baseToolbarDecoratedActivity = baseToolbarDecoratedActivity;
    }

    @Provides ToolbarDecorator provideToolbarDecorator() {
        return baseToolbarDecoratedActivity.getToolbarDecorator();
    }
}
