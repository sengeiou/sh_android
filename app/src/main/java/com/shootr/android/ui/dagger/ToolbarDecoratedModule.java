package com.shootr.android.ui.dagger;

import com.shootr.android.ShootrModule;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.activities.BaseToolbarDecoratedActivity;
import com.shootr.android.ui.fragments.ProfileFragment;
import com.shootr.android.ui.fragments.StreamTimelineFragment;
import com.shootr.android.ui.fragments.StreamsListFragment;
import dagger.Module;
import dagger.Provides;

@Module(
  injects = {
    StreamsListFragment.class,
    StreamTimelineFragment.class,
    ProfileFragment.class,
  },
  addsTo = ShootrModule.class
)
public class ToolbarDecoratedModule {

  private final BaseToolbarDecoratedActivity baseToolbarDecoratedActivity;

  public ToolbarDecoratedModule(BaseToolbarDecoratedActivity baseToolbarDecoratedActivity) {
    this.baseToolbarDecoratedActivity = baseToolbarDecoratedActivity;
  }

  @Provides ToolbarDecorator provideToolbarDecorator() {
    return baseToolbarDecoratedActivity.getToolbarDecorator();
  }
}
