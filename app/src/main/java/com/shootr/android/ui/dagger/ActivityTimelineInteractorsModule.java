package com.shootr.android.ui.dagger;

import com.shootr.android.ShootrModule;
import com.shootr.android.ui.fragments.ActivityTimelineFragment;
import com.shootr.android.ui.presenter.TimelinePresenter;
import com.shootr.android.ui.presenter.interactorwrapper.ActivityTimelineInteractorsWrapper;
import com.shootr.android.ui.presenter.interactorwrapper.TimelineInteractorsWrapper;
import dagger.Module;
import dagger.Provides;

@Module(
  injects = {
    TimelinePresenter.class, ActivityTimelineFragment.class
  },
  addsTo = ShootrModule.class
)
public final class ActivityTimelineInteractorsModule {

    @Provides TimelineInteractorsWrapper provideTimelineInteractorsWrapper(
      ActivityTimelineInteractorsWrapper activityTimelineInteractorsWrapper) {
        return activityTimelineInteractorsWrapper;
    }
}
