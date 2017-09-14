package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.ShootrValidationException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.AddSuggestedfavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.GetOnBoardingStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.StreamsListInteractor;
import com.shootr.mobile.domain.model.FollowableType;
import com.shootr.mobile.domain.model.stream.OnBoarding;
import com.shootr.mobile.domain.model.stream.StreamSearchResultList;
import com.shootr.mobile.ui.model.OnBoardingModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.mappers.OnBoardingStreamModelMapper;
import com.shootr.mobile.ui.views.OnBoardingView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class OnBoardingStreamPresenter implements Presenter {

  public static final String STREAM_ONBOARDING = "streamOnBoarding";
  public static final String USER_ONBOARDING = "streamOnBoarding";

  private final StreamsListInteractor streamsListInteractor;
  private final GetOnBoardingStreamInteractor getOnBoardingStreamInteractor;
  private final AddSuggestedfavoritesInteractor addSuggestedfavoritesInteractor;
  private final ErrorMessageFactory errorMessageFactory;
  private final OnBoardingStreamModelMapper onBoardingStreamModelMapper;

  private OnBoardingView onBoardingView;
  private boolean streamsLoaded = false;
  private boolean getStartedClicked = false;
  private HashMap<String, StreamModel> favoriteStreams = new HashMap<>();

  @Inject public OnBoardingStreamPresenter(StreamsListInteractor streamsListInteractor,
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

  public void initialize(OnBoardingView welcomePageView, String onBoardingType) {
    setView(welcomePageView);
    if (onBoardingType.equals(STREAM_ONBOARDING)) {
      loadOnBoardingStreams();
      loadDefaultStreams();
    } else {
      //TODO cargar users
    }
  }

  private void loadOnBoardingStreams() {
    onBoardingView.showLoading();
    getOnBoardingStreamInteractor.loadOnBoardingStreams(FollowableType.STREAM,
        new Interactor.Callback<List<OnBoarding>>() {
          @Override public void onLoaded(List<OnBoarding> onBoardingStreams) {
            onBoardingView.hideLoading();
            if (!onBoardingStreams.isEmpty()) {
              List<OnBoardingModel> onBoardingStreamModels =
                  onBoardingStreamModelMapper.transform(onBoardingStreams);
              storeDefaultFavorites(onBoardingStreamModels);
              onBoardingView.renderOnBoardingList(
                  onBoardingStreamModelMapper.transform(onBoardingStreams));
            } else {
              onBoardingView.goToUserOnboardingList();
            }
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            showViewError(error);
          }
        });
  }

  private void storeDefaultFavorites(List<OnBoardingModel> onBoardingStreamModels) {
    for (OnBoardingModel onBoardingStreamModel : onBoardingStreamModels) {
      if (onBoardingStreamModel.getStreamModel() != null && onBoardingStreamModel.isFavorite()) {
        putFavorite(onBoardingStreamModel);
      }
    }
  }

  private void loadDefaultStreams() {
    streamsListInteractor.loadStreams(new Interactor.Callback<StreamSearchResultList>() {
      @Override public void onLoaded(StreamSearchResultList streamSearchResultList) {
        streamsLoaded = true;
        if (getStartedClicked) {
          onBoardingView.goToUserOnboardingList();
        }
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        showViewError(error);
      }
    });
  }

  public void continueClicked() {
    getStartedClicked = true;
    sendFavorites();
  }

  private void sendFavorites() {
    if (!favoriteStreams.isEmpty()) {
      ArrayList<String> itemsIds = new ArrayList<>(favoriteStreams.keySet());
      addSuggestedfavoritesInteractor.addSuggestedFavorites(itemsIds,
          new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
              sendStreamAnalytics();
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

  private void sendStreamAnalytics() {
    for (Map.Entry<String, StreamModel> entry : favoriteStreams.entrySet()) {
      onBoardingView.sendStreamAnalytics(entry.getKey(), entry.getValue().getTitle(),
          entry.getValue().isStrategic());
    }
  }

  public void putFavorite(OnBoardingModel onBoardingModel) {
    StreamModel streamModel = onBoardingModel.getStreamModel();
    if (!favoriteStreams.containsKey(streamModel.getIdStream())) {
      favoriteStreams.put(streamModel.getIdStream(), streamModel);
    }
  }

  public void removeFavorite(String idStream) {
    if (favoriteStreams.containsKey(idStream)) {
      favoriteStreams.remove(idStream);
    }
  }

  private void checkStreamsLoaded() {
    onBoardingView.hideGetStarted();
    if (streamsLoaded) {
      onBoardingView.goToUserOnboardingList();
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
