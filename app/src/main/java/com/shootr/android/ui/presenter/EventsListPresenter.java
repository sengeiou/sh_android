package com.shootr.android.ui.presenter;

import com.shootr.android.data.mapper.EventResultModelMapper;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.EventSearchResultList;
import com.shootr.android.domain.interactor.EventsListInteractor;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.views.EventsListView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.Arrays;
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

    private void loadDefaultEventList() {
        eventsListInteractor.loadEvents();
    }

    @Subscribe
    public void onDefaultEventListLoaded(EventSearchResultList resultList) {
        List<EventSearchResult> events = resultList.getEventSearchResults();
        List<EventResultModel> eventModels = eventResultModelMapper.transform(events);
        eventsListView.renderEvents(eventModels);
    }


    //region Lifecycle
    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }
    //endregion
}
