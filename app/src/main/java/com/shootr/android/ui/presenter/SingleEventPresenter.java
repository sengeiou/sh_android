package com.shootr.android.ui.presenter;

import android.support.annotation.Nullable;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.bus.WatchUpdateRequest;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventInfo;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.EventNotificationInteractor;
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
import com.shootr.android.ui.views.SingleEventView;
import com.shootr.android.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import javax.inject.Inject;

public class SingleEventPresenter implements Presenter, CommunicationPresenter {

    //region Dependencies
    private final @Main Bus bus;
    private final VisibleEventInfoInteractor eventInfoInteractor;
    private final WatchingInteractor watchingInteractor;
    private final EventNotificationInteractor eventNotificationInteractor;
    private final EventsWatchedCountInteractor eventsWatchedCountInteractor;
    private final SelectEventInteractor selectEventInteractor;

    private final EventModelMapper eventModelMapper;
    private final UserWatchingModelMapper userWatchingModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private SingleEventView singleEventView;

    private UserWatchingModel currentUserWatchingModel;
    private EventModel eventModel;
    private int watchersCount;

    @Inject public SingleEventPresenter(@Main Bus bus, VisibleEventInfoInteractor eventInfoInteractor,
      WatchingInteractor watchingInteractor, EventNotificationInteractor eventNotificationInteractor,
      EventsWatchedCountInteractor eventsWatchedCountInteractor, SelectEventInteractor selectEventInteractor, EventModelMapper eventModelMapper, UserWatchingModelMapper userWatchingModelMapper,
      ErrorMessageFactory errorMessageFactory) {
        this.bus = bus;
        this.eventInfoInteractor = eventInfoInteractor;
        this.watchingInteractor = watchingInteractor;
        this.eventNotificationInteractor = eventNotificationInteractor;
        this.eventsWatchedCountInteractor = eventsWatchedCountInteractor;
        this.selectEventInteractor = selectEventInteractor;
        this.eventModelMapper = eventModelMapper;
        this.userWatchingModelMapper = userWatchingModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }
    //endregion

    public void initialize(SingleEventView singleEventView) {
        this.singleEventView = singleEventView;
        this.loadEventInfo();
        this.loadEventsCount();
    }

    //region interaction methods
    public void editStatus() {
        singleEventView.navigateToEditStatus(eventModel, currentUserWatchingModel);
    }

    public void resultFromEditStatus(@Nullable String statusText) {
        updateWatch(currentUserWatchingModel.isWatching(), statusText);
    }

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

    public void sendWatching(boolean isWatching) {
        updateWatch(isWatching, currentUserWatchingModel.getPlace());
    }

    public void editEvent() {
        Long idEvent = eventModel.getIdEvent();
        singleEventView.navigateToEditEvent(idEvent);
    }

    public void resultFromEditEvent(Long idEventEdited) {
        if (idEventEdited.equals(eventModel.getIdEvent())) {
            loadEventInfo();
        }
    }

    private void updateWatch(boolean isWatching, String statusText) {
        watchingInteractor.sendWatching(isWatching, eventModel.getIdEvent(), statusText,
          new WatchingInteractor.Callback() {
              @Override public void onLoaded(Watch watchUpdated) {
                  updateWatchersCount(watchUpdated.isWatching());
                  renderCurrentUserWatching(watchUpdated);
              }
          });
    }

    private void onEventChanged() {
        this.loadEventInfo();
    }

    public void toggleNotifications() {
        boolean enableNotifications = !currentUserWatchingModel.isNotificationsEnabled();
        eventNotificationInteractor.setNotificationEnabledForEvent(enableNotifications, eventModel.getIdEvent());
        //TODO handle some response maybe?
        currentUserWatchingModel.setNotificationsEnabled(enableNotifications);
        singleEventView.setNotificationsEnabled(enableNotifications);
        this.showNotificationsAlert(enableNotifications);
    }

    @Subscribe
    public void onNewWatchDetected(WatchUpdateRequest.Event event) {
        this.getEventInfo();
        this.loadEventsCount();
    }

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

    public void loadEventInfo() {
        this.showViewLoading();
        this.getEventInfo();
    }

    private void getEventInfo() {
        eventInfoInteractor.obtainEventInfo(new VisibleEventInfoInteractor.Callback() {
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
            watchersCount = eventInfo.getWatchersCount();
            this.renderWatchersCount(watchersCount);
            this.showViewDetail();
        }
        this.hideViewLoading();
    }

    private void showViewDetail() {
        singleEventView.showContent();
        singleEventView.showDetail();
    }

    private void updateWatchersCount(boolean isWatching) {
        watchersCount = isWatching ? watchersCount+1 : watchersCount-1;
        renderWatchersCount(watchersCount);
    }

    private void showNotificationsAlert(boolean enableNotifications) {
        if (enableNotifications) {
            singleEventView.alertNotificationsEnabled();
        } else {
            singleEventView.alertNotificationsDisabled();
        }
    }
    //endregion

    //region renders
    private void renderWatchersList(List<Watch> watchers) {
        List<UserWatchingModel> watcherModels = userWatchingModelMapper.transform(watchers);
        singleEventView.setWatchers(watcherModels);
    }

    private void renderCurrentUserWatching(Watch currentUserWatch) {
        currentUserWatchingModel = userWatchingModelMapper.transform(currentUserWatch);
        singleEventView.setCurrentUserWatching(currentUserWatchingModel);
        singleEventView.setIsWatching(currentUserWatchingModel.isWatching());
        singleEventView.setNotificationsEnabled(currentUserWatchingModel.isNotificationsEnabled());
    }

    private void renderEventInfo(Event event) {
        eventModel = eventModelMapper.transform(event);
        singleEventView.setEventTitle(eventModel.getTitle());
        singleEventView.setEventDate(eventModel.getDatetime());
        singleEventView.setEventPicture(eventModel.getPicture());
        //TODO only if I'm author, else hide
        singleEventView.showEditEventButton();
    }

    private void renderWatchersCount(int watchersCount) {
        singleEventView.setWatchersCount(watchersCount);
    }

    private void renderEventsCount(int eventsCount) {
        singleEventView.setEventsCount(eventsCount);
    }
    //endregion

    //region View methods
    private void showViewLoading() {
        singleEventView.hideContent();
        singleEventView.showLoading();
    }

    private void hideViewLoading() {
        singleEventView.hideLoading();
    }

    private void showViewEmpty() {
        singleEventView.showContent();
        singleEventView.showEmpty();
    }

    private void hideViewEmpty() {
        singleEventView.hideEmpty();
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
        singleEventView.showError(communicationErrorMessage);
    }

    @Subscribe @Override public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        String connectionNotAvailableMessage = errorMessageFactory.getConnectionNotAvailableMessage();
        singleEventView.showError(connectionNotAvailableMessage);
    }

    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }
}
