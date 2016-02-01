package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.StreamSearchResult;
import com.shootr.mobile.domain.StreamSearchResultList;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.ShootrValidationException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.StreamSearchInteractor;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.model.mappers.StreamResultModelMapper;
import com.shootr.mobile.ui.views.FindStreamsView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class FindStreamsPresenter implements Presenter {

    private final StreamSearchInteractor streamSearchInteractor;
    private final AddToFavoritesInteractor addToFavoritesInteractor;
    private final ShareStreamInteractor shareStreamInteractor;
    private final StreamResultModelMapper streamResultModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private FindStreamsView findStreamsView;
    private String lastQueryText;
    private boolean hasBeenPaused = false;

    @Inject public FindStreamsPresenter(StreamSearchInteractor streamSearchInteractor,
      AddToFavoritesInteractor addToFavoritesInteractor, ShareStreamInteractor shareStreamInteractor,
      StreamResultModelMapper streamResultModelMapper, ErrorMessageFactory errorMessageFactory) {
        this.streamSearchInteractor = streamSearchInteractor;
        this.addToFavoritesInteractor = addToFavoritesInteractor;
        this.shareStreamInteractor = shareStreamInteractor;
        this.streamResultModelMapper = streamResultModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }

    public void setView(FindStreamsView findStreamsView) {
        this.findStreamsView = findStreamsView;
    }

    public void initialize(FindStreamsView findStreamsView) {
        this.setView(findStreamsView);
    }

    public void search(String queryText) {
        this.lastQueryText = queryText;
        findStreamsView.hideContent();
        findStreamsView.hideKeyboard();
        findStreamsView.showLoading();
        streamSearchInteractor.searchStreams(queryText, new StreamSearchInteractor.Callback() {
            @Override public void onLoaded(StreamSearchResultList results) {
                onSearchResults(results);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                findStreamsView.hideLoading();
                showViewError(error);
            }
        });
    }

    public void selectStream(StreamResultModel stream) {
        selectStream(stream.getStreamModel().getIdStream(), stream.getStreamModel().getShortTitle(), stream.getStreamModel().getAuthorId());
    }

    private void selectStream(final String idStream, String streamShortTitle, String authorId) {
        findStreamsView.navigateToStreamTimeline(idStream, streamShortTitle, authorId);
    }

    private void onSearchResults(StreamSearchResultList streamSearchResultList) {
        List<StreamSearchResult> streamSearchResults = streamSearchResultList.getStreamSearchResults();
        if (!streamSearchResults.isEmpty()) {
            List<StreamResultModel> streamModels = streamResultModelMapper.transform(streamSearchResults);
            renderViewStreamsList(streamModels);
        } else {
            this.showViewEmpty();
        }
        findStreamsView.hideLoading();
    }

    private void showViewEmpty() {
        findStreamsView.showEmpty();
        findStreamsView.hideContent();
    }

    private void renderViewStreamsList(List<StreamResultModel> streamModels) {
        findStreamsView.showContent();
        findStreamsView.hideEmpty();
        findStreamsView.renderStreams(streamModels);
    }

    private void showViewError(ShootrException error) {
        String errorMessage;
        if (error instanceof ShootrValidationException) {
            String errorCode = ((ShootrValidationException) error).getErrorCode();
            errorMessage = errorMessageFactory.getMessageForCode(errorCode);
        } else {
            errorMessage = errorMessageFactory.getMessageForError(error);
        }
        findStreamsView.showError(errorMessage);
    }

    public void restoreStreams(List<StreamResultModel> restoredResults) {
        if (restoredResults != null && !restoredResults.isEmpty()) {
            findStreamsView.renderStreams(restoredResults);
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

    public void addToFavorites(StreamResultModel stream) {
        addToFavoritesInteractor.addToFavorites(stream.getStreamModel().getIdStream(), new Interactor.CompletedCallback() {
              @Override
              public void onCompleted() {
                  findStreamsView.showAddedToFavorites();
              }
          }, new Interactor.ErrorCallback() {
            @Override
            public void onError(ShootrException error) {
                showViewError(error);
            }
        });
    }

    public void shareStream(StreamResultModel stream) {
        shareStreamInteractor.shareStream(stream.getStreamModel().getIdStream(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                findStreamsView.showStreamShared();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showViewError(error);
            }
        });
    }
}
