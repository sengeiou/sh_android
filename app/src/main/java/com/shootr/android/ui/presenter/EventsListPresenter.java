package com.shootr.android.ui.presenter;

import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.StreamSearchResultList;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.exception.ShootrValidationException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.StreamsListInteractor;
import com.shootr.android.domain.interactor.event.UnwatchStreamInteractor;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.model.mappers.StreamResultModelMapper;
import com.shootr.android.ui.views.EventsListView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class EventsListPresenter implements Presenter {

    private final StreamsListInteractor streamsListInteractor;
    private final UnwatchStreamInteractor unwatchStreamInteractor;
    private final StreamResultModelMapper streamResultModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private EventsListView eventsListView;
    private boolean hasBeenPaused;

    @Inject public EventsListPresenter(StreamsListInteractor streamsListInteractor,
      UnwatchStreamInteractor unwatchStreamInteractor, StreamResultModelMapper streamResultModelMapper, ErrorMessageFactory errorMessageFactory) {
        this.streamsListInteractor = streamsListInteractor;
        this.unwatchStreamInteractor = unwatchStreamInteractor;
        this.streamResultModelMapper = streamResultModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }
    //endregion

    public void setView(EventsListView eventsListView) {
        this.eventsListView = eventsListView;
    }

    public void initialize(EventsListView eventsListView) {
        this.setView(eventsListView);
        this.loadDefaultEventList();
    }

    public void refresh() {
        this.loadDefaultEventList();
    }

    public void selectEvent(StreamResultModel event) {
        eventsListView.setCurrentWatchingEventId(event);
        selectEvent(event.getStreamModel().getIdStream(), event.getStreamModel().getTag());
    }

    private void selectEvent(final String idEvent, String eventTitle) {
        eventsListView.navigateToEventTimeline(idEvent, eventTitle);
    }

    public void unwatchEvent() {
        unwatchStreamInteractor.unwatchStream(new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                loadDefaultEventList();
                removeCurrentWatchingEvent();
                eventsListView.showNotificationsOff();
            }
        });
    }

    private void removeCurrentWatchingEvent() {
        eventsListView.setCurrentWatchingEventId(null);
    }

    protected void loadDefaultEventList() {
        streamsListInteractor.loadStreams(new Interactor.Callback<StreamSearchResultList>() {
            @Override public void onLoaded(StreamSearchResultList streamSearchResultList) {
                eventsListView.hideLoading();
                onDefaultEventListLoaded(streamSearchResultList);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showViewError(error);
            }
        });
    }

    public void onDefaultEventListLoaded(StreamSearchResultList resultList) {
        List<StreamSearchResult> streamSearchResults = resultList.getStreamSearchResults();
        if (!streamSearchResults.isEmpty()) {
            List<StreamResultModel> streamResultModels = streamResultModelMapper.transform(streamSearchResults);
            this.renderViewEventsList(streamResultModels);
            StreamSearchResult currentWatchingEvent = resultList.getCurrentWatchingStream();
            this.setViewCurrentVisibleWatchingEvent(streamResultModelMapper.transform(currentWatchingEvent));
        }else{
            eventsListView.showLoading();
        }
    }

    public void eventCreated(String eventId, String eventTitle) {
        selectEvent(eventId, eventTitle);
    }

    private void setViewCurrentVisibleWatchingEvent(StreamResultModel currentVisibleEvent) {
        eventsListView.setCurrentWatchingEventId(currentVisibleEvent);
    }

    private void renderViewEventsList(List<StreamResultModel> eventModels) {
        eventsListView.showContent();
        eventsListView.hideEmpty();
        eventsListView.renderEvents(eventModels);
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
