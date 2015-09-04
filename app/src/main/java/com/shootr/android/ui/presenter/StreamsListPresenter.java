package com.shootr.android.ui.presenter;

import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.StreamSearchResultList;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.exception.ShootrValidationException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.android.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.android.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.android.domain.interactor.stream.StreamsListInteractor;
import com.shootr.android.domain.interactor.stream.UnwatchStreamInteractor;
import com.shootr.android.domain.service.StreamIsAlreadyInFavoritesException;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.model.mappers.StreamResultModelMapper;
import com.shootr.android.ui.views.StreamsListView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class StreamsListPresenter implements Presenter {

    private final StreamsListInteractor streamsListInteractor;
    private final AddToFavoritesInteractor addToFavoritesInteractor;
    private final UnwatchStreamInteractor unwatchStreamInteractor;
    private final SelectStreamInteractor selectStreamInteractor;
    private final ShareStreamInteractor shareStreamInteractor;
    private final StreamResultModelMapper streamResultModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private StreamsListView streamsListView;
    private boolean hasBeenPaused;

    @Inject public StreamsListPresenter(StreamsListInteractor streamsListInteractor,
      AddToFavoritesInteractor addToFavoritesInteractor,
      UnwatchStreamInteractor unwatchStreamInteractor,
      SelectStreamInteractor selectStreamInteractor,
      ShareStreamInteractor shareStreamInteractor,
      StreamResultModelMapper streamResultModelMapper,
      ErrorMessageFactory errorMessageFactory) {
        this.streamsListInteractor = streamsListInteractor;
        this.addToFavoritesInteractor = addToFavoritesInteractor;
        this.unwatchStreamInteractor = unwatchStreamInteractor;
        this.selectStreamInteractor = selectStreamInteractor;
        this.shareStreamInteractor = shareStreamInteractor;
        this.streamResultModelMapper = streamResultModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }
    //endregion

    public void setView(StreamsListView streamsListView) {
        this.streamsListView = streamsListView;
    }

    public void initialize(StreamsListView streamsListView) {
        this.setView(streamsListView);
        this.loadDefaultStreamList();
    }

    public void refresh() {
        this.loadDefaultStreamList();
    }

    public void selectStream(StreamResultModel stream) {
        streamsListView.setCurrentWatchingStreamId(stream);
        selectStream(stream.getStreamModel().getIdStream(), stream.getStreamModel().getTag());
    }

    private void selectStream(final String idStream, String streamTag) {
        streamsListView.navigateToStreamTimeline(idStream, streamTag);
    }

    public void unwatchStream() {
        unwatchStreamInteractor.unwatchStream(new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                loadDefaultStreamList();
                removeCurrentWatchingStream();
            }
        });
    }

    private void removeCurrentWatchingStream() {
        streamsListView.setCurrentWatchingStreamId(null);
    }

    protected void loadDefaultStreamList() {
        streamsListInteractor.loadStreams(new Interactor.Callback<StreamSearchResultList>() {
            @Override public void onLoaded(StreamSearchResultList streamSearchResultList) {
                streamsListView.hideLoading();
                onDefaultStreamListLoaded(streamSearchResultList);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showViewError(error);
            }
        });
    }

    public void onDefaultStreamListLoaded(StreamSearchResultList resultList) {
        List<StreamSearchResult> streamSearchResults = resultList.getStreamSearchResults();
        if (!streamSearchResults.isEmpty()) {
            List<StreamResultModel> streamResultModels = streamResultModelMapper.transform(streamSearchResults);
            this.renderViewStreamsList(streamResultModels);
            StreamSearchResult currentWatchingStream = resultList.getCurrentWatchingStream();
            this.setViewCurrentVisibleWatchingStream(streamResultModelMapper.transform(currentWatchingStream));
        }else{
            streamsListView.showLoading();
        }
    }

    public void streamCreated(String streamId) {
        streamsListView.navigateToCreatedStreamDetail(streamId);
    }

    private void setViewCurrentVisibleWatchingStream(StreamResultModel currentVisibleStream) {
        streamsListView.setCurrentWatchingStreamId(currentVisibleStream);
    }

    private void renderViewStreamsList(List<StreamResultModel> streamModels) {
        streamsListView.showContent();
        streamsListView.hideEmpty();
        streamsListView.renderStream(streamModels);
    }

    private void showViewError(ShootrException error) {
        String errorMessage;
        if (error instanceof ShootrValidationException) {
            String errorCode = ((ShootrValidationException) error).getErrorCode();
            errorMessage = errorMessageFactory.getMessageForCode(errorCode);
        } else if (error instanceof ServerCommunicationException) {
            errorMessage = errorMessageFactory.getCommunicationErrorMessage();
        } else if (error instanceof StreamIsAlreadyInFavoritesException) {
            errorMessage = errorMessageFactory.getStreamIsAlreadyInFavoritesError();
        } else {
            errorMessage = errorMessageFactory.getUnknownErrorMessage();
        }
        streamsListView.showError(errorMessage);
    }

    public void onCommunicationError() {
        streamsListView.showError(errorMessageFactory.getCommunicationErrorMessage());
    }

    public void addToFavorites(StreamResultModel streamResultModel) {
        addToFavoritesInteractor.addToFavorites(streamResultModel.getStreamModel().getIdStream(),
          new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                  streamsListView.showAddedToFavorites();
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  showViewError(error);
              }
          });
    }

    //region Lifecycle
    @Override public void resume() {
        if (hasBeenPaused) {
            this.loadDefaultStreamList();
        }
    }

    @Override public void pause() {
        hasBeenPaused = true;
    }

    public void shareStream(StreamResultModel stream) {
        shareStreamInteractor.shareStream(stream.getStreamModel().getIdStream(), new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                  streamsListView.showStreamShared();
              }
          }, new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  showViewError(error);
              }
          });
    }

    //endregion
}
