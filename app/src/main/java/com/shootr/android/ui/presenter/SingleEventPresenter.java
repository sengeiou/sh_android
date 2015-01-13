package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventInfo;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.interactor.VisibleEventInfoInteractor;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.model.UserWatchingModel;
import com.shootr.android.ui.model.mappers.EventModelMapper;
import com.shootr.android.ui.model.mappers.UserWatchingModelMapper;
import com.shootr.android.ui.views.SingleEventView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import javax.inject.Inject;

public class SingleEventPresenter implements Presenter, CommunicationPresenter {

    private final Bus bus;
    private final VisibleEventInfoInteractor eventInfoInteractor;
    private final EventModelMapper eventModelMapper;
    private final UserWatchingModelMapper userWatchingModelMapper;

    private SingleEventView singleEventView;

    @Inject public SingleEventPresenter(Bus bus, VisibleEventInfoInteractor eventInfoInteractor,
      EventModelMapper eventModelMapper, UserWatchingModelMapper userWatchingModelMapper) {
        this.bus = bus;
        this.eventInfoInteractor = eventInfoInteractor;
        this.eventModelMapper = eventModelMapper;
        this.userWatchingModelMapper = userWatchingModelMapper;
    }

    public void initialize(SingleEventView singleEventView) {
        this.singleEventView = singleEventView;
        this.loadEventInfo();
    }

    private void loadEventInfo() {
        this.showViewLoading();
        this.hideEmptyView();
        this.getEventInfo();
    }

    private void getEventInfo() {
        eventInfoInteractor.obtainEventInfo();
    }

    @Subscribe
    public void onEventInfoLoaded(EventInfo eventInfo) {
        this.hideViewLoading();
        this.renderEvent(eventInfo.getEvent());
        this.renderCurrentUserWatching(eventInfo.getCurrentUserWatch());
        this.renderWatchersList(eventInfo.getWatchers());
        this.renderWatchersCount(eventInfo.getWatchersCount());
    }

    private void renderWatchersList(List<Watch> watchers) {
        List<UserWatchingModel> watcherModels = userWatchingModelMapper.transform(watchers);
        singleEventView.setWatchers(watcherModels);
    }

    private void renderCurrentUserWatching(Watch currentUserWatch) {
        UserWatchingModel currentUserWatchingModel = userWatchingModelMapper.transform(currentUserWatch);
        singleEventView.setCurrentUserWatching(currentUserWatchingModel);
    }

    private void renderEvent(Event event) {
        MatchModel eventModel = eventModelMapper.transform(event);
        singleEventView.setEventTitle(eventModel.getTitle());
        singleEventView.setEventDate(eventModel.getDatetime());
    }

    private void renderWatchersCount(int watchersCount) {
        singleEventView.setWatchersCount(watchersCount);
    }

    private void showViewLoading() {

    }

    private void hideViewLoading() {

    }

    private void hideEmptyView() {
    }

    @Override public void onCommunicationError(CommunicationErrorEvent event) {

    }

    @Override public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {

    }

    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }
}
