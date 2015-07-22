package com.shootr.android.ui.presenter;

import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.StreamSearchResultList;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.exception.ShootrValidationException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.StreamSearchInteractor;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.model.mappers.StreamResultModelMapper;
import com.shootr.android.ui.views.FindEventsView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class FindEventsPresenter implements Presenter {

    private final StreamSearchInteractor streamSearchInteractor;
    private final StreamResultModelMapper streamResultModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private FindEventsView findEventsView;
    private String lastQueryText;
    private boolean hasBeenPaused = false;

    @Inject public FindEventsPresenter(StreamSearchInteractor streamSearchInteractor,
      StreamResultModelMapper streamResultModelMapper, ErrorMessageFactory errorMessageFactory) {
        this.streamSearchInteractor = streamSearchInteractor;
        this.streamResultModelMapper = streamResultModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }

    public void setView(FindEventsView findEventsView) {
        this.findEventsView = findEventsView;
    }

    public void initialize(FindEventsView findEventsView) {
        this.setView(findEventsView);
    }

    public void search(String queryText) {
        this.lastQueryText = queryText;
        findEventsView.hideContent();
        findEventsView.hideKeyboard();
        findEventsView.showLoading();
        streamSearchInteractor.searchStreams(queryText, new StreamSearchInteractor.Callback() {
            @Override public void onLoaded(StreamSearchResultList results) {
                onSearchResults(results);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                findEventsView.hideLoading();
                showViewError(error);
            }
        });
    }

    public void selectEvent(StreamResultModel event) {
        selectEvent(event.getStreamModel().getIdStream(), event.getStreamModel().getTitle());
    }

    private void selectEvent(final String idEvent, String eventTitle) {
        findEventsView.navigateToEventTimeline(idEvent, eventTitle);
    }

    private void onSearchResults(StreamSearchResultList streamSearchResultList) {
        List<StreamSearchResult> streamSearchResults = streamSearchResultList.getStreamSearchResults();
        if (!streamSearchResults.isEmpty()) {
            List<StreamResultModel> eventModels = streamResultModelMapper.transform(streamSearchResults);
            renderViewEventsList(eventModels);
        } else {
            this.showViewEmpty();
        }
        findEventsView.hideLoading();
    }

    private void showViewEmpty() {
        findEventsView.showEmpty();
        findEventsView.hideContent();
    }

    private void renderViewEventsList(List<StreamResultModel> eventModels) {
        findEventsView.showContent();
        findEventsView.hideEmpty();
        findEventsView.renderEvents(eventModels);
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
        findEventsView.showError(errorMessage);
    }

    public void restoreEvents(List<StreamResultModel> restoredResults) {
        if (restoredResults != null && !restoredResults.isEmpty()) {
            findEventsView.renderEvents(restoredResults);
        }
    }

    @Override public void resume() {
        if (hasBeenPaused && lastQueryText != null) {
            search(lastQueryText);
        }
    }

    @Override public void pause() {
        hasBeenPaused = true;
    }
}
