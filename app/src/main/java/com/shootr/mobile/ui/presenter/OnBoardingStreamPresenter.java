package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.ShootrValidationException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.AddSuggestedfavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.GetOnBoardingStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.StreamsListInteractor;
import com.shootr.mobile.domain.model.stream.OnBoardingStream;
import com.shootr.mobile.domain.model.stream.StreamSearchResultList;
import com.shootr.mobile.ui.model.OnBoardingStreamModel;
import com.shootr.mobile.ui.model.mappers.OnBoardingStreamModelMapper;
import com.shootr.mobile.ui.views.OnBoardingView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class OnBoardingStreamPresenter implements Presenter {

  private final StreamsListInteractor streamsListInteractor;
  private final GetOnBoardingStreamInteractor getOnBoardingStreamInteractor;
  private final AddSuggestedfavoritesInteractor addSuggestedfavoritesInteractor;
  private final ErrorMessageFactory errorMessageFactory;
  private final OnBoardingStreamModelMapper onBoardingStreamModelMapper;

  private OnBoardingView onBoardingView;
  private boolean streamsLoaded = false;
  private boolean getStartedClicked = false;
  private ArrayList<String> favoritesIdStream = new ArrayList<>();
  private HashMap<String, String> favoritesInfo = new HashMap<>();

  @Inject
  public OnBoardingStreamPresenter(StreamsListInteractor streamsListInteractor,
      GetOnBoardingStreamInteractor getOnBoardingStreamInteractor,
      AddSuggestedfavoritesInteractor addSuggestedfavoritesInteractor,
      ErrorMessageFactory errorMessageFactory,
      OnBoardingStreamModelMapper onBoardingStreamModelMapper) {
    this.streamsListInteractor = streamsListInteractor;
    this.getOnBoardingStreamInteractor = getOnBoardingStreamInteractor;
    this.addSuggestedfavoritesInteractor = addSuggestedfavoritesInteractor;
    this.errorMessageFactory = errorMessageFactory;
    this.onBoardingStreamModelMapper = onBoardingStreamModelMapper;
  }

  protected void setView(OnBoardingView welcomePageView) {
    this.onBoardingView = welcomePageView;
  }

  public void initialize(OnBoardingView welcomePageView) {
    setView(welcomePageView);
    loadOnBoardingStreams();
    loadDefaultStreams();
  }

  private void loadOnBoardingStreams() {
    onBoardingView.showLoading();
    getOnBoardingStreamInteractor.loadMutedStreamsIdsFromLocal(
        new Interactor.Callback<List<OnBoardingStream>>() {
          @Override public void onLoaded(List<OnBoardingStream> onBoardingStreams) {
            onBoardingView.hideLoading();
            if (!onBoardingStreams.isEmpty()) {
              List<OnBoardingStreamModel> onBoardingStreamModels =
                  onBoardingStreamModelMapper.transform(onBoardingStreams);
              storeDefaultFavorites(onBoardingStreamModels);
              onBoardingView.renderOnBoardingList(
                  onBoardingStreamModelMapper.transform(onBoardingStreams));
            } else {
              onBoardingView.goToStreamList();
            }
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            showViewError(error);
          }
        });
  }

  private void storeDefaultFavorites(List<OnBoardingStreamModel> onBoardingStreamModels) {
    for (OnBoardingStreamModel onBoardingStreamModel : onBoardingStreamModels) {
      if (onBoardingStreamModel.isFavorite()) {
        favoritesIdStream.add(onBoardingStreamModel.getStreamModel().getIdStream());
        favoritesInfo.put(onBoardingStreamModel.getStreamModel().getIdStream(),
            onBoardingStreamModel.getStreamModel().getTitle());
      }
    }
  }

  private void loadDefaultStreams() {
    streamsListInteractor.loadStreams(new Interactor.Callback<StreamSearchResultList>() {
      @Override public void onLoaded(StreamSearchResultList streamSearchResultList) {
        streamsLoaded = true;
        if (getStartedClicked) {
          onBoardingView.goToStreamList();
        }
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        showViewError(error);
      }
    });
  }

  public void getStartedClicked() {
    getStartedClicked = true;
    sendFavorites();
  }

  private void sendFavorites() {
    if (!favoritesIdStream.isEmpty()) {
      addSuggestedfavoritesInteractor.addSuggestedFavorites(favoritesIdStream, new Interactor.CompletedCallback() {
        @Override public void onCompleted() {
          sendAnalytics();
          checkStreamsLoaded();
        }
      }, new Interactor.ErrorCallback() {
        @Override public void onError(ShootrException error) {
          showViewError(error);
        }
      });
    } else {
      checkStreamsLoaded();
    }
  }

  private void sendAnalytics() {
    for (Map.Entry<String, String> entry : favoritesInfo.entrySet()) {
      onBoardingView.sendAnalytics(entry.getKey(), entry.getValue());
    }
  }

  public void putFavorite(String idStream, String streamTitle) {
    if (!favoritesIdStream.contains(idStream)) {
      favoritesIdStream.add(idStream);
      favoritesInfo.put(idStream, streamTitle);
    }
  }

  public void removeFavorite(String idStream) {
    if (favoritesIdStream.contains(idStream)) {
      favoritesIdStream.remove(idStream);
      favoritesInfo.remove(idStream);
    }
  }

  private void checkStreamsLoaded() {
    onBoardingView.hideGetStarted();
    if (streamsLoaded) {
      onBoardingView.goToStreamList();
    } else {
      onBoardingView.showLoading();
    }
  }

  private void showViewError(ShootrException error) {
    String errorMessage;
    if (error instanceof ShootrValidationException) {
      String errorCode = ((ShootrValidationException) error).getErrorCode();
      errorMessage = errorMessageFactory.getMessageForCode(errorCode);
    } else {
      errorMessage = errorMessageFactory.getMessageForError(error);
    }
    onBoardingView.showError(errorMessage);
  }

  @Override public void resume() {
    /* no-op */
  }

  @Override public void pause() {
    /* no-op */
  }
}
