package com.shootr.android.ui.presenter;

import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.bus.EventChanged;
import com.shootr.android.domain.bus.ShotSent;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.timeline.GetMainTimelineInteractor;
import com.shootr.android.domain.interactor.timeline.GetOlderMainTimelineInteractor;
import com.shootr.android.domain.interactor.timeline.RefreshMainTimelineInteractor;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.views.TimelineView;
import com.shootr.android.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import javax.inject.Inject;

public class TimelinePresenter implements Presenter, ShotSent.Receiver, EventChanged.Receiver {

    private final GetMainTimelineInteractor getMainTimelineInteractor;
    private final RefreshMainTimelineInteractor refreshMainTimelineInteractor;
    private final GetOlderMainTimelineInteractor getOlderMainTimelineInteractor;
    private final ShotModelMapper shotModelMapper;
    private final Bus bus;
    private final ErrorMessageFactory errorMessageFactory;

    private TimelineView timelineView;
    private boolean isLoadingOlderShots;
    private boolean mightHaveMoreShots = true;

    @Inject public TimelinePresenter(GetMainTimelineInteractor getMainTimelineInteractor,
      RefreshMainTimelineInteractor refreshMainTimelineInteractor,
      GetOlderMainTimelineInteractor getOlderMainTimelineInteractor, ShotModelMapper shotModelMapper, @Main Bus bus,
      ErrorMessageFactory errorMessageFactory) {
        this.getMainTimelineInteractor = getMainTimelineInteractor;
        this.refreshMainTimelineInteractor = refreshMainTimelineInteractor;
        this.getOlderMainTimelineInteractor = getOlderMainTimelineInteractor;
        this.shotModelMapper = shotModelMapper;
        this.bus = bus;
        this.errorMessageFactory = errorMessageFactory;
    }

    public void setView(TimelineView timelineView) {
        this.timelineView = timelineView;
    }

    public void initialize(TimelineView timelineView) {
        this.setView(timelineView);
        this.loadMainTimeline();
    }

    public void loadMainTimeline() {
        timelineView.showLoading();
        getMainTimelineInteractor.loadMainTimeline(new GetMainTimelineInteractor.Callback() {
            @Override public void onLoaded(Timeline timeline) {
                List<ShotModel> shotModels = shotModelMapper.transform(timeline.getShots());
                timelineView.hideLoading();
                timelineView.setShots(shotModels);
                if (!shotModels.isEmpty()) {
                    timelineView.hideEmpty();
                    timelineView.showShots();
                } else {
                    timelineView.showEmpty();
                    timelineView.hideShots();
                }
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                timelineView.hideLoading();
                timelineView.showError(errorMessageFactory.getCommunicationErrorMessage());
            }
        });
    }

    public void refresh() {
        timelineView.showLoading();
        refreshMainTimelineInteractor.refreshMainTimeline(new RefreshMainTimelineInteractor.Callback() {
            @Override public void onLoaded(Timeline timeline) {
                List<ShotModel> shotModels = shotModelMapper.transform(timeline.getShots());
                if (!shotModels.isEmpty()) {
                    timelineView.addNewShots(shotModels);
                    timelineView.hideEmpty();
                    timelineView.showShots();
                }
                timelineView.hideLoading();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                timelineView.showError(errorMessageFactory.getCommunicationErrorMessage());
                timelineView.hideLoading();
            }
        });
    }

    public void showingLastShot(ShotModel lastShot) {
        if (!isLoadingOlderShots && mightHaveMoreShots) {
            this.loadOlderShots(lastShot.getCsysBirth().getTime());
        }
    }

    private void loadOlderShots(long lastShotInScreenDate) {
        isLoadingOlderShots = true;
        timelineView.showLoadingOldShots();
        getOlderMainTimelineInteractor.loadOlderMainTimeline(lastShotInScreenDate,
          new GetOlderMainTimelineInteractor.Callback() {
              @Override public void onLoaded(Timeline timeline) {
                  isLoadingOlderShots = false;
                  timelineView.hideLoadingOldShots();
                  List<ShotModel> shotModels = shotModelMapper.transform(timeline.getShots());
                  if (!shotModels.isEmpty()) {
                      timelineView.addOldShots(shotModels);
                  } else {
                      mightHaveMoreShots = false;
                  }
              }
          }, new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  timelineView.hideLoadingOldShots();
                  timelineView.showError(errorMessageFactory.getCommunicationErrorMessage());
              }
          });
    }

    @Override public void resume() {
        refresh();
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }

    @Subscribe
    @Override public void onShotSent(ShotSent.Event event) {
        refresh();
    }

    @Subscribe
    @Override public void onEventChanged(EventChanged.Event event) {
        loadMainTimeline();
    }
}
