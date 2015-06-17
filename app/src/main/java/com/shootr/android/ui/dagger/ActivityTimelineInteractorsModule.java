package com.shootr.android.ui.dagger;

import com.shootr.android.ShootrModule;
import com.shootr.android.ui.fragments.ActivityTimelineFragment;
import com.shootr.android.ui.presenter.ActivityTimelinePresenter;
import com.shootr.android.ui.presenter.interactorwrapper.ActivityTimelineInteractorWrapper;
import com.shootr.android.ui.presenter.interactorwrapper.ActivityTimelineInteractorsWrapper;
import dagger.Module;
import dagger.Provides;

@Module(
  injects = {
    ActivityTimelineFragment.class, ActivityTimelinePresenter.class
  },
  addsTo = ShootrModule.class
)
public final class ActivityTimelineInteractorsModule {

    @Provides ActivityTimelineInteractorWrapper provideActivityTimelineInteractorsWrapper(
      ActivityTimelineInteractorsWrapper activityTimelineInteractorsWrapper) {
        return activityTimelineInteractorsWrapper;
    }
}
