package com.shootr.android.ui.presenter;

import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.exception.ShootrValidationException;
import com.shootr.android.domain.interactor.event.EventsSearchInteractor;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.ui.model.mappers.EventResultModelMapper;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.EventSearchResultList;
import com.shootr.android.domain.interactor.event.EventsListInteractor;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.views.EventsListView;
import com.shootr.android.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import javax.inject.Inject;

public class EventsListPresenter implements Presenter, CommunicationPresenter{

    //region Dependencies
    private final Bus bus;
    private final EventsListInteractor eventsListInteractor;
    private final EventsSearchInteractor eventsSearchInteractor;
    private final EventResultModelMapper eventResultModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private EventsListView eventsListView;

    @Inject public EventsListPresenter(@Main Bus bus, EventsListInteractor eventsListInteractor,
      EventsSearchInteractor eventsSearchInteractor, EventResultModelMapper eventResultModelMapper, ErrorMessageFactory errorMessageFactory) {
        this.bus = bus;
        this.eventsListInteractor = eventsListInteractor;
        this.eventsSearchInteractor = eventsSearchInteractor;
        this.eventResultModelMapper = eventResultModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }
    //endregion

    public void initialize(EventsListView eventsListView) {
        this.eventsListView = eventsListView;
        this.loadDefaultEventList();
    }

    public void initialize(EventsListView eventsListView, String initialQuery) {
        this.eventsListView = eventsListView;
        this.search(initialQuery);
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

    public void search(String queryText) {
        eventsListView.hideKeyboard();
        eventsSearchInteractor.searchEvents(queryText, new EventsSearchInteractor.Callback() {
            @Override public void onLoaded(EventSearchResultList results) {
                onSearchResults(results);
            }
        },
        new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showViewError(error);
            }
        });
    }

    public void eventCreated(String eventId) {
        eventsListView.closeScrenWithEventResult(eventId);
    }

    private void onSearchResults(EventSearchResultList eventSearchResultList) {
        showEventListInView(eventSearchResultList);
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
        eventsListView.hideEmpty();
        eventsListView.renderEvents(eventModels);
    }

    private void showViewEmpty() {
        eventsListView.showEmpty();
        eventsListView.hideContent();
    }

    private void showViewError(ShootrException error) {
        String errorMessage;
        if (error instanceof ShootrValidationException) {
            String errorCode = ((ShootrValidationException) error).getErrorCode();
            errorMessage = errorMessageFactory.getMessageForCode(errorCode);
        } else {
            errorMessage = errorMessageFactory.getUnknownErrorMessage();
        }
        eventsListView.showError(errorMessage);
    }

    @Subscribe
    @Override public void onCommunicationError(CommunicationErrorEvent event) {
        eventsListView.showError(errorMessageFactory.getCommunicationErrorMessage());
    }

    @Subscribe
    @Override public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        eventsListView.showError(errorMessageFactory.getConnectionNotAvailableMessage());
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
