package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.ShootrValidationException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.GetLocalStreamsInteractor;
import com.shootr.mobile.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.StreamReactiveSearchInteractor;
import com.shootr.mobile.domain.interactor.stream.StreamSearchInteractor;
import com.shootr.mobile.domain.interactor.stream.UnwatchStreamInteractor;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.model.stream.StreamSearchResultList;
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
  private final GetLocalStreamsInteractor getLocalStreamsInteractor;
  private final StreamReactiveSearchInteractor streamReactiveSearchInteractor;
  private final UnwatchStreamInteractor unwatchStreamInteractor;
  private final StreamResultModelMapper streamResultModelMapper;
  private final ErrorMessageFactory errorMessageFactory;

  private FindStreamsView findStreamsView;
  private String lastQueryText;
  private boolean hasBeenPaused = false;
  private List<StreamResultModel> streamModels;

  @Inject public FindStreamsPresenter(StreamSearchInteractor streamSearchInteractor,
      AddToFavoritesInteractor addToFavoritesInteractor,
      ShareStreamInteractor shareStreamInteractor,
      GetLocalStreamsInteractor getLocalStreamsInteractor,
      StreamReactiveSearchInteractor streamReactiveSearchInteractor,
      UnwatchStreamInteractor unwatchStreamInteractor,
      StreamResultModelMapper streamResultModelMapper, ErrorMessageFactory errorMessageFactory) {
    this.streamSearchInteractor = streamSearchInteractor;
    this.addToFavoritesInteractor = addToFavoritesInteractor;
    this.shareStreamInteractor = shareStreamInteractor;
    this.getLocalStreamsInteractor = getLocalStreamsInteractor;
    this.streamReactiveSearchInteractor = streamReactiveSearchInteractor;
    this.unwatchStreamInteractor = unwatchStreamInteractor;
    this.streamResultModelMapper = streamResultModelMapper;
    this.errorMessageFactory = errorMessageFactory;
  }

  public void setView(FindStreamsView findStreamsView) {
    this.findStreamsView = findStreamsView;
  }

  public void initialize(final FindStreamsView findStreamsView) {
    this.setView(findStreamsView);
    loadStreams();
  }

  private void loadStreams() {
    getLocalStreamsInteractor.loadStreams(new Interactor.Callback<StreamSearchResultList>() {
      @Override public void onLoaded(StreamSearchResultList streamSearchResultList) {
        onSearchResults(streamSearchResultList);
      }
    });
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
    selectStream(stream.getStreamModel().getIdStream(), stream.getStreamModel().getTitle(),
        stream.getStreamModel().getAuthorId());
  }

  private void selectStream(final String idStream, String streamTitle, String authorId) {
    findStreamsView.navigateToStreamTimeline(idStream, streamTitle, authorId);
  }

  private void onSearchResults(StreamSearchResultList streamSearchResultList) {
    List<StreamSearchResult> streamSearchResults = streamSearchResultList.getStreamSearchResults();
    if (!streamSearchResults.isEmpty()) {
      streamModels = streamResultModelMapper.transform(streamSearchResults);
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

  public void addToFavorites(StreamResultModel stream) {
    addToFavoritesInteractor.addToFavorites(stream.getStreamModel().getIdStream(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            findStreamsView.showAddedToFavorites();
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            showViewError(error);
          }
        });
  }

  public void shareStream(StreamResultModel stream) {
    shareStreamInteractor.shareStream(stream.getStreamModel().getIdStream(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            findStreamsView.showStreamShared();
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            showViewError(error);
          }
        });
  }

  public void reactiveSearch(final String query) {
    streamReactiveSearchInteractor.searchStreams(query,
        new Interactor.Callback<StreamSearchResultList>() {
          @Override public void onLoaded(StreamSearchResultList streamSearchResultList) {
            lastQueryText = query;
            onSearchResults(streamSearchResultList);
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {

          }
        });
  }

  public void unwatchStream() {
    unwatchStreamInteractor.unwatchStream(new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        if (lastQueryText != null) {
          for (StreamResultModel streamModel : streamModels) {
            if (streamModel.isWatching()) {
              streamModel.setIsWatching(false);
              renderViewStreamsList(streamModels);
            }
          }
        } else {
          loadStreams();
        }
      }
    });
  }

  private void loadSearchedStreams() {
    streamSearchInteractor.searchStreams(lastQueryText, new StreamSearchInteractor.Callback() {
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

  @Override public void resume() {
    if (hasBeenPaused && lastQueryText != null) {
      if (lastQueryText.length() < 3) {
        reactiveSearch(lastQueryText);
      } else {
        loadSearchedStreams();
      }
    } else if (hasBeenPaused) {
      loadStreams();
    }
    findStreamsView.hideKeyboard();
  }

  @Override public void pause() {
    hasBeenPaused = true;
  }
}
