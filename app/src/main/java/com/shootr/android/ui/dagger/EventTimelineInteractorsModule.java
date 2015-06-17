package com.shootr.android.ui.dagger;

import com.shootr.android.ShootrModule;
import com.shootr.android.ui.fragments.EventTimelineFragment;
import com.shootr.android.ui.presenter.TimelinePresenter;
import com.shootr.android.ui.presenter.interactorwrapper.EventTimelineInteractorWrapper;
import com.shootr.android.ui.presenter.interactorwrapper.TimelineInteractorWrapper;
import dagger.Module;
import dagger.Provides;

@Module(
  injects = {
    TimelinePresenter.class, EventTimelineFragment.class
  },
  addsTo = ShootrModule.class
)
public final class EventTimelineInteractorsModule {

    @Provides TimelineInteractorWrapper provideTimelineInteractorsWrapper(
      EventTimelineInteractorWrapper eventTimelineInteractorsWrapper) {
        return eventTimelineInteractorsWrapper;
    }
}
