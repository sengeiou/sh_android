package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.EventSearchResultList;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.exception.ShootrValidationException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.EventsListInteractor;
import com.shootr.android.domain.interactor.event.EventsSearchInteractor;
import com.shootr.android.domain.interactor.event.SelectEventInteractor;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.model.mappers.EventModelMapper;
import com.shootr.android.ui.model.mappers.EventResultModelMapper;
import com.shootr.android.ui.views.EventsListView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class EventsListPresenter implements Presenter {

    private final EventsListInteractor eventsListInteractor;
    private final EventsSearchInteractor eventsSearchInteractor;
    private final SelectEventInteractor selectEventInteractor;
    private final EventResultModelMapper eventResultModelMapper;
    private final EventModelMapper eventModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private EventsListView eventsListView;
    private boolean hasBeenPaused;

    @Inject public EventsListPresenter(EventsListInteractor eventsListInteractor,
      EventsSearchInteractor eventsSearchInteractor, SelectEventInteractor selectEventInteractor, EventResultModelMapper eventResultModelMapper,
      EventModelMapper eventModelMapper, ErrorMessageFactory errorMessageFactory) {
        this.eventsListInteractor = eventsListInteractor;
        this.eventsSearchInteractor = eventsSearchInteractor;
        this.selectEventInteractor = selectEventInteractor;
        this.eventResultModelMapper = eventResultModelMapper;
        this.eventModelMapper = eventModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }
    //endregion

    protected void setView(EventsListView eventsListView) {
        this.eventsListView = eventsListView;
    }

    public void initialize(EventsListView eventsListView) {
        this.setView(eventsListView);
        this.loadDefaultEventList();
    }

    public void initialize(EventsListView eventsListView, String initialQuery) {
        this.eventsListView = eventsListView;
        this.search(initialQuery);
    }

    public void refresh() {
        this.loadDefaultEventList();
    }

    public void selectEvent(EventModel event) {
        selectEvent(event.getIdEvent(), event.getTitle());
    }

    private void selectEvent(final String idEvent, String eventTitle) {
        selectEventInteractor.selectEvent(idEvent, new Interactor.Callback<Event>() {
            @Override public void onLoaded(Event selectedEvent) {
                onEventSelected(eventModelMapper.transform(selectedEvent));
            }
        });
    }

    private void onEventSelected(EventModel selectedEvent) {
        eventsListView.navigateToEventTimeline(selectedEvent.getIdEvent(), selectedEvent.getTitle());
    }

    protected void loadDefaultEventList() {
        eventsListView.showLoading();
        eventsListInteractor.loadEvents(new Interactor.Callback<EventSearchResultList>() {
            @Override public void onLoaded(EventSearchResultList eventSearchResultList) {
                onDefaultEventListLoaded(eventSearchResultList);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showViewError(error);
            }
        });
    }

    public void onDefaultEventListLoaded(EventSearchResultList resultList) {
        List<EventSearchResult> eventSearchResults = resultList.getEventSearchResults();
        if (!eventSearchResults.isEmpty()) {
            List<EventResultModel> eventResultModels = eventResultModelMapper.transform(eventSearchResults);
            eventsListView.hideLoading();
            this.renderViewEventsList(eventResultModels);
            this.setViewCurrentVisibleEvent(resultList.getCurrentCheckedInEventId());
        }
    }

    public void search(String queryText) {
        eventsListView.hideKeyboard();
        eventsSearchInteractor.searchEvents(queryText, new EventsSearchInteractor.Callback() {
            @Override public void onLoaded(EventSearchResultList results) {
                onSearchResults(results);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showViewError(error);
            }
        });
    }

    public void eventCreated(String eventId, String eventTitle) {
        selectEvent(eventId, eventTitle);
    }

    private void onSearchResults(EventSearchResultList eventSearchResultList) {
        List<EventSearchResult> eventSearchResults = eventSearchResultList.getEventSearchResults();
        if (!eventSearchResults.isEmpty()) {
            List<EventResultModel> eventModels = eventResultModelMapper.transform(eventSearchResults);
            renderViewEventsList(eventModels);
        } else {
            this.showViewEmpty();
        }
    }

    private void setViewCurrentVisibleEvent(String currentVisibleEventId) {
        eventsListView.setCurrentVisibleEventId(currentVisibleEventId);
    }

    private void renderViewEventsList(List<EventResultModel> eventModels) {
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
        } else if (error instanceof ServerCommunicationException) {
            errorMessage = errorMessageFactory.getCommunicationErrorMessage();
        } else {
            errorMessage = errorMessageFactory.getUnknownErrorMessage();
        }
        eventsListView.showError(errorMessage);
    }

    public void onCommunicationError() {
        eventsListView.showError(errorMessageFactory.getCommunicationErrorMessage());
    }

    public void onConnectionNotAvailable() {
        eventsListView.showError(errorMessageFactory.getConnectionNotAvailableMessage());
    }

    //region Lifecycle
    @Override public void resume() {
        if (hasBeenPaused) {
            this.loadDefaultEventList();
        }
    }

    @Override public void pause() {
        hasBeenPaused = true;
    }
    //endregion
}
