package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Event;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.mappers.EventResultModelMapper;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.EventSearchResultList;
import com.shootr.android.domain.interactor.EventsListInteractor;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.views.EventsListView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import javax.inject.Inject;

public class EventsListPresenter implements Presenter {

    //region Dependencies
    private final Bus bus;
    private final EventsListInteractor eventsListInteractor;
    private final EventResultModelMapper eventResultModelMapper;

    private EventsListView eventsListView;

    @Inject public EventsListPresenter(Bus bus, EventsListInteractor eventsListInteractor,
      EventResultModelMapper eventResultModelMapper) {
        this.bus = bus;
        this.eventsListInteractor = eventsListInteractor;
        this.eventResultModelMapper = eventResultModelMapper;
    }
    //endregion

    public void initialize(EventsListView eventsListView) {
        this.eventsListView = eventsListView;
        this.loadDefaultEventList();
    }

    public void selectEvent(EventModel event) {
        eventsListView.closeScrenWithEventResult(event.getIdEvent());
    }

    private void loadDefaultEventList() {
        eventsListView.showLoading();
        eventsListInteractor.loadEvents();
    }

    @Subscribe
    public void onDefaultEventListLoaded(EventSearchResultList resultList) {
        eventsListView.hideLoading();
        this.setViewCurrentVisibleEvent(resultList);
        this.showEventListInView(resultList);
    }

    private void setViewCurrentVisibleEvent(EventSearchResultList resultList) {
        Event currentVisibleEvent = resultList.getCurrentVisibleEvent();
        if (currentVisibleEvent != null) {
            eventsListView.setCurrentVisibleEventId(currentVisibleEvent.getId());
        }
    }

    private void showEventListInView(EventSearchResultList resultList) {
        List<EventSearchResult> events = resultList.getEventSearchResults();
        if (events.size() > 0) {
            this.renderViewEventsList(events);
        } else {
            this.showViewEmpty();
        }
    }

    private void renderViewEventsList(List<EventSearchResult> events) {
        List<EventResultModel> eventModels = eventResultModelMapper.transform(events);
        eventsListView.showContent();
        eventsListView.renderEvents(eventModels);
    }

    private void showViewEmpty() {
        eventsListView.showEmpty();
        eventsListView.hideContent();
    }

    //TODO errores

    //region Lifecycle
    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }
    //endregion
}
