package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventInfo;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.interactor.EventsCountInteractor;
import com.shootr.android.domain.interactor.NotificationInteractor;
import com.shootr.android.domain.interactor.VisibleEventInfoInteractor;
import com.shootr.android.domain.interactor.WatchingInteractor;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.ui.model.MatchModel;
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

    private final Bus bus;
    private final VisibleEventInfoInteractor eventInfoInteractor;
    private final WatchingInteractor watchingInteractor;
    private final NotificationInteractor notificationInteractor;
    private final EventsCountInteractor eventsCountInteractor;

    private final EventModelMapper eventModelMapper;
    private final UserWatchingModelMapper userWatchingModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private SingleEventView singleEventView;

    private UserWatchingModel currentUserWatchingModel;
    private MatchModel eventModel;

    @Inject public SingleEventPresenter(Bus bus, VisibleEventInfoInteractor eventInfoInteractor,
      WatchingInteractor watchingInteractor, NotificationInteractor notificationInteractor,
      EventsCountInteractor eventsCountInteractor, EventModelMapper eventModelMapper, UserWatchingModelMapper userWatchingModelMapper,
      ErrorMessageFactory errorMessageFactory) {
        this.bus = bus;
        this.eventInfoInteractor = eventInfoInteractor;
        this.watchingInteractor = watchingInteractor;
        this.notificationInteractor = notificationInteractor;
        this.eventsCountInteractor = eventsCountInteractor;
        this.eventModelMapper = eventModelMapper;
        this.userWatchingModelMapper = userWatchingModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }

    public void initialize(SingleEventView singleEventView) {
        this.singleEventView = singleEventView;
        this.loadEventInfo();
        this.loadEventsCount();
    }

    public void edit() {
        singleEventView.navigateToEdit(eventModel, currentUserWatchingModel);
    }

    public void sendWatching(boolean isWatching) {
        watchingInteractor.sendWatching(isWatching, eventModel.getIdMatch(), null);

        //TODO probably better to receive the new Watch from the Interactor
        currentUserWatchingModel.setWatching(isWatching);
        singleEventView.setCurrentUserWatching(currentUserWatchingModel);
        singleEventView.setIsWatching(currentUserWatchingModel.isWatching());
        singleEventView.setNotificationsEnabled(currentUserWatchingModel.isWatching()); //TODO this is bussines logic
    }

    public void toggleNotifications() {
        boolean enableNotifications = !currentUserWatchingModel.isNotificationsEnabled();
        notificationInteractor.setNotificationEnabledForEvent(enableNotifications, eventModel.getIdMatch());
        //TODO handle some response maybe?
        currentUserWatchingModel.setNotificationsEnabled(enableNotifications);
        singleEventView.setNotificationsEnabled(enableNotifications);
        this.showNotificationsAlert(enableNotifications);
    }

    private void loadEventsCount() {
        eventsCountInteractor.obtainEventsCount();
    }

    @Subscribe public void onEventsCountLoaded(Integer count) {
        renderEventsCount(count);
    }

    public void loadEventInfo() {
        this.showViewLoading();
        this.getEventInfo();
    }

    private void getEventInfo() {
        eventInfoInteractor.obtainEventInfo();
    }

    @Subscribe
    public void onEventInfoLoaded(EventInfo eventInfo) {
        if (eventInfo.getEvent() == null) {
            this.showViewEmpty();
        } else {
            this.hideViewEmpty();
            this.renderEvent(eventInfo.getEvent());
            this.renderCurrentUserWatching(eventInfo.getCurrentUserWatch());
            this.renderWatchersList(eventInfo.getWatchers());
            this.renderWatchersCount(eventInfo.getWatchersCount());
            this.showViewContent();
        }
        this.hideViewLoading();
    }

    private void showNotificationsAlert(boolean enableNotifications) {
        if (enableNotifications) {
            singleEventView.alertNotificationsEnabled();
        } else {
            singleEventView.alertNotificationsDisabled();
        }
    }

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

    private void renderEvent(Event event) {
        eventModel = eventModelMapper.transform(event);
        singleEventView.setEventTitle(eventModel.getTitle());
        singleEventView.setEventDate(eventModel.getDatetime());
    }

    private void renderWatchersCount(int watchersCount) {
        singleEventView.setWatchersCount(watchersCount);
    }

    private void renderEventsCount(int eventsCount) {
        singleEventView.setEventsCount(eventsCount);
    }

    private void showViewContent() {
        singleEventView.showContent();
    }

    private void showViewLoading() {
        singleEventView.showLoading();
    }

    private void hideViewLoading() {
        singleEventView.hideLoading();
    }

    private void showViewEmpty() {
        singleEventView.showEmpty();
    }

    private void hideViewEmpty() {
        singleEventView.hideEmpty();
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
