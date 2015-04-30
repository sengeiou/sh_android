package com.shootr.android.ui.dagger;

import com.shootr.android.ShootrModule;
import com.shootr.android.ui.fragments.EventTimelineFragment;
import com.shootr.android.ui.presenter.TimelinePresenter;
import com.shootr.android.ui.presenter.interactorwrapper.EventTimelineInteractorsWrapper;
import com.shootr.android.ui.presenter.interactorwrapper.TimelineInteractorsWrapper;
import dagger.Module;
import dagger.Provides;

@Module(
  injects = {
    TimelinePresenter.class, EventTimelineFragment.class
  },
  addsTo = ShootrModule.class
)
public final class EventTimelineInteractorsModule {

    @Provides TimelineInteractorsWrapper provideTimelineInteractorsWrapper(
      EventTimelineInteractorsWrapper eventTimelineInteractorsWrapper) {
        return eventTimelineInteractorsWrapper;
    }
}
