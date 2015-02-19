package com.shootr.android.ui.presenter;

import android.support.annotation.Nullable;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.bus.WatchUpdateRequest;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventInfo;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.ChangeEventPhotoInteractor;
import com.shootr.android.domain.interactor.event.EventsWatchedCountInteractor;
import com.shootr.android.domain.interactor.event.SelectEventInteractor;
import com.shootr.android.domain.interactor.event.VisibleEventInfoInteractor;
import com.shootr.android.domain.interactor.event.WatchingInteractor;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.UserWatchingModel;
import com.shootr.android.ui.model.mappers.EventModelMapper;
import com.shootr.android.ui.model.mappers.UserWatchingModelMapper;
import com.shootr.android.ui.views.EventDetailView;
import com.shootr.android.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.io.File;
import java.util.List;
import javax.inject.Inject;

public class EventDetailPresenter implements Presenter, CommunicationPresenter {

    //region Dependencies
    private final Bus bus;
    private final VisibleEventInfoInteractor eventInfoInteractor;
    private final WatchingInteractor watchingStatusInteractor;
    private final EventsWatchedCountInteractor eventsWatchedCountInteractor;
    private final SelectEventInteractor selectEventInteractor;
    private final ChangeEventPhotoInteractor changeEventPhotoInteractor;

    private final EventModelMapper eventModelMapper;
    private final UserWatchingModelMapper userWatchingModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private EventDetailView eventDetailView;
    private long idEvent;

    private UserWatchingModel currentUserWatchingModel;
    private EventModel eventModel;

    @Inject public EventDetailPresenter(@Main Bus bus, VisibleEventInfoInteractor eventInfoInteractor,
      WatchingInteractor watchingStatusInteractor, EventsWatchedCountInteractor eventsWatchedCountInteractor,
      SelectEventInteractor selectEventInteractor, ChangeEventPhotoInteractor changeEventPhotoInteractor,
      EventModelMapper eventModelMapper, UserWatchingModelMapper userWatchingModelMapper,
      ErrorMessageFactory errorMessageFactory) {
        this.bus = bus;
        this.eventInfoInteractor = eventInfoInteractor;
        this.watchingStatusInteractor = watchingStatusInteractor;
        this.eventsWatchedCountInteractor = eventsWatchedCountInteractor;
        this.selectEventInteractor = selectEventInteractor;
        this.changeEventPhotoInteractor = changeEventPhotoInteractor;
        this.eventModelMapper = eventModelMapper;
        this.userWatchingModelMapper = userWatchingModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }
    //endregion

    public void initialize(EventDetailView eventDetailView, long idEvent) {
        this.eventDetailView = eventDetailView;
        this.idEvent = idEvent;
        this.loadEventInfo();
        this.loadEventsCount();
    }

    //region Edit status
    public void editStatus() {
        eventDetailView.navigateToEditStatus(eventModel, currentUserWatchingModel);
    }

    public void resultFromEditStatus(@Nullable String statusText) {
        updateWatchStatus(statusText);
    }
    //endregion

    //region Select event
    public void resultFromSelectEvent(Long idEventSelected) {
        if (!isCurrentEventWatch(idEventSelected)) {
            this.showViewLoading();
            selectEventInteractor.selectEvent(idEventSelected, new SelectEventInteractor.Callback() {
                @Override public void onLoaded(Watch watch) {
                    onEventChanged();
                }
            });
        }
    }

    private void onEventChanged() {
        this.loadEventInfo();
    }
    //endregion

    private void updateWatchStatus(String statusText) {
        watchingStatusInteractor.sendWatching(eventModel.getIdEvent(), statusText, new WatchingInteractor.Callback() {
              @Override public void onLoaded(Watch watchUpdated) {
                  renderCurrentUserWatching(watchUpdated);
              }
          });
    }

    //region Edit event
    public void editEvent() {
        Long idEvent = eventModel.getIdEvent();
        eventDetailView.navigateToEditEvent(idEvent);
    }

    public void resultFromEditEvent(Long idEventEdited) {
        if (idEventEdited.equals(eventModel.getIdEvent())) {
            loadEventInfo();
        }
    }

    //endregion

    public void editPhoto() {
        eventDetailView.showPhotoPicker();
    }

    public void photoSelected(File photoFile) {
        eventDetailView.showLoadingPictureUpload();
        changeEventPhotoInteractor.changeEventPhoto(eventModel.getIdEvent(), photoFile,
          new ChangeEventPhotoInteractor.Callback() {
              @Override public void onLoaded(Event event) {
                  renderEventInfo(event);
                  eventDetailView.hideLoadingPictureUpload();
                  eventDetailView.showEditPicture(event.getPicture());
              }
          }, new Interactor.InteractorErrorCallback() {
              @Override public void onError(ShootrException error) {
                  eventDetailView.showEditPicture(eventModel.getPicture());
                  eventDetailView.hideLoadingPictureUpload();
                  showImageUploadError();
              }
          });
    }

    //region Events count
    private void loadEventsCount() {
        eventsWatchedCountInteractor.obtainEventsCount(new EventsWatchedCountInteractor.Callback() {
            @Override public void onLoaded(Integer count) {
                onEventsCountLoaded(count);
            }
        }, new Interactor.InteractorErrorCallback() {
            @Override public void onError(ShootrException error) {
                //TODO handle error
            }
        });
    }

    public void onEventsCountLoaded(Integer count) {
        renderEventsCount(count);
    }
    //endregion

    //region Event info
    public void refreshInfo() {
        this.getEventInfo();
    }

    public void loadEventInfo() {
        this.showViewLoading();
        this.getEventInfo();
    }

    private void getEventInfo() {
        eventInfoInteractor.obtainEventInfo(idEvent, new VisibleEventInfoInteractor.Callback() {
            @Override public void onLoaded(EventInfo eventInfo) {
                onEventInfoLoaded(eventInfo);
            }
        });
    }

    public void onEventInfoLoaded(EventInfo eventInfo) {
        if (eventInfo.getEvent() == null) {
            this.showViewEmpty();
        } else {
            this.hideViewEmpty();
            this.renderEventInfo(eventInfo.getEvent());
            this.renderWatchersList(eventInfo.getWatchers());
            this.renderCurrentUserWatching(eventInfo.getCurrentUserWatch());
            this.renderWatchersCount(eventInfo.getWatchersCount());
            this.showViewDetail();
        }
        this.hideViewLoading();
    }

    private void showViewDetail() {
        eventDetailView.showContent();
        eventDetailView.showDetail();
    }
    //endregion

    public void clickAuthor() {
        eventDetailView.navigateToUser(eventModel.getAuthorId());
    }

    public void photoClick() {
        if (eventModel.amIAuthor()) {
            if (eventModel.getPicture() == null) {
                editPhoto();
            } else {
                eventDetailView.showViewOrEditPhoto();
            }
        } else {
            zoomPhoto();
        }
    }

    public void zoomPhoto() {
        eventDetailView.zoomPhoto(eventModel.getPicture());
    }

    @Subscribe
    public void onNewWatchDetected(WatchUpdateRequest.Event event) {
        this.getEventInfo();
        this.loadEventsCount();
    }

    //region renders
    private void renderWatchersList(List<Watch> watchers) {
        List<UserWatchingModel> watcherModels = userWatchingModelMapper.transform(watchers);
        eventDetailView.setWatchers(watcherModels);
    }

    private void renderCurrentUserWatching(Watch currentUserWatch) {
        if (currentUserWatch != null && currentUserWatch.isVisible()) {
            currentUserWatchingModel = userWatchingModelMapper.transform(currentUserWatch);
            eventDetailView.setCurrentUserWatching(currentUserWatchingModel);
        }
    }

    private void renderEventInfo(Event event) {
        eventModel = eventModelMapper.transform(event);
        eventDetailView.setEventTitle(eventModel.getTitle());
        eventDetailView.setEventDate(eventModel.getDatetime());
        eventDetailView.setEventPicture(eventModel.getPicture());
        eventDetailView.setEventAuthor(eventModel.getAuthorUsername());
        if (eventModel.amIAuthor()) {
            eventDetailView.showEditEventButton();
            eventDetailView.showEditPicture(eventModel.getPicture());
        } else {
            eventDetailView.hideEditEventButton();
            eventDetailView.hideEditPicture();
        }
    }

    private void renderWatchersCount(int watchersCount) {
        eventDetailView.setWatchersCount(watchersCount);
    }

    private void renderEventsCount(int eventsCount) {
        eventDetailView.setEventsCount(eventsCount);
    }
    //endregion

    //region View methods
    private void showViewLoading() {
        eventDetailView.hideContent();
        eventDetailView.showLoading();
    }

    private void hideViewLoading() {
        eventDetailView.hideLoading();
    }

    private void showViewEmpty() {
        eventDetailView.showContent();
        eventDetailView.showEmpty();
    }

    private void hideViewEmpty() {
        eventDetailView.hideEmpty();
    }
    //endregion

    private boolean isCurrentEventWatch(Long idEvent) {
        if (eventModel == null) {
            return false;
        } else {
            return idEvent.equals(eventModel.getIdEvent());
        }
    }

    @Subscribe @Override public void onCommunicationError(CommunicationErrorEvent event) {
        String communicationErrorMessage = errorMessageFactory.getCommunicationErrorMessage();
        eventDetailView.showError(communicationErrorMessage);
    }

    @Subscribe @Override public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        String connectionNotAvailableMessage = errorMessageFactory.getConnectionNotAvailableMessage();
        eventDetailView.showError(connectionNotAvailableMessage);
    }

    private void showImageUploadError() {
        eventDetailView.showError(errorMessageFactory.getImageUploadErrorMessage());
    }

    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }
}
