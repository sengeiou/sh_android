package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.ShootrValidationException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.AddSuggestedfavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.GetOnBoardingStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.StreamsListInteractor;
import com.shootr.mobile.domain.interactor.user.GetOnBoardingUserInteractor;
import com.shootr.mobile.domain.model.FollowableType;
import com.shootr.mobile.domain.model.stream.OnBoarding;
import com.shootr.mobile.domain.model.stream.StreamSearchResultList;
import com.shootr.mobile.ui.model.OnBoardingModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.OnBoardingModelMapper;
import com.shootr.mobile.ui.views.OnBoardingView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class OnBoardingPresenter implements Presenter {

  public static final String STREAM_ONBOARDING = "streamOnBoarding";
  public static final String USER_ONBOARDING = "userOnBoarding";

  private final StreamsListInteractor streamsListInteractor;
  private final GetOnBoardingStreamInteractor getOnBoardingStreamInteractor;
  private final GetOnBoardingUserInteractor getOnBoardingUserInteractor;
  private final AddSuggestedfavoritesInteractor addSuggestedfavoritesInteractor;
  private final ErrorMessageFactory errorMessageFactory;
  private final OnBoardingModelMapper onBoardingModelMapper;

  private OnBoardingView onBoardingView;
  private boolean streamsLoaded = false;
  private boolean getStartedClicked = false;
  private HashMap<String, StreamModel> favoriteStreams = new HashMap<>();
  private HashMap<String, UserModel> favoriteUsers = new HashMap<>();

  @Inject public OnBoardingPresenter(StreamsListInteractor streamsListInteractor,
      GetOnBoardingStreamInteractor getOnBoardingStreamInteractor,
      GetOnBoardingUserInteractor getOnBoardingUserInteractor,
      AddSuggestedfavoritesInteractor addSuggestedfavoritesInteractor,
      ErrorMessageFactory errorMessageFactory, OnBoardingModelMapper onBoardingModelMapper) {
    this.streamsListInteractor = streamsListInteractor;
    this.getOnBoardingStreamInteractor = getOnBoardingStreamInteractor;
    this.getOnBoardingUserInteractor = getOnBoardingUserInteractor;
    this.addSuggestedfavoritesInteractor = addSuggestedfavoritesInteractor;
    this.errorMessageFactory = errorMessageFactory;
    this.onBoardingModelMapper = onBoardingModelMapper;
  }

  protected void setView(OnBoardingView welcomePageView) {
    this.onBoardingView = welcomePageView;
  }

  public void initialize(OnBoardingView welcomePageView, String onBoardingType) {
    setView(welcomePageView);
    if (onBoardingType.equals(STREAM_ONBOARDING)) {
      loadOnBoardingStreams();
    } else {
      loadOnBoardingUsers();
      loadDefaultStreams();
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
                  onBoardingModelMapper.transform(onBoardingStreams, FollowableType.STREAM);
              storeDefaultFavorites(onBoardingStreamModels);
              onBoardingView.renderOnBoardingList(onBoardingStreamModels);
            } else {
              onBoardingView.goNextScreen();
            }
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            showViewError(error);
          }
        });
  }

  private void loadOnBoardingUsers() {
    onBoardingView.showLoading();
    getOnBoardingUserInteractor.loadOnBoardingUsers(FollowableType.USER,
        new Interactor.Callback<List<OnBoarding>>() {
          @Override public void onLoaded(List<OnBoarding> onBoardingUsers) {
            onBoardingView.hideLoading();
            if (!onBoardingUsers.isEmpty()) {
              List<OnBoardingModel> onBoardingUsersModels =
                  onBoardingModelMapper.transform(onBoardingUsers, FollowableType.USER);
              storeDefaultFavorites(onBoardingUsersModels);
              onBoardingView.renderOnBoardingList(onBoardingUsersModels);
            } else {
              onBoardingView.goNextScreen();
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
          onBoardingView.goNextScreen();
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
      addSuggestedfavoritesInteractor.addSuggestedFavorites(itemsIds, FollowableType.STREAM,
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
    } else if (!favoriteUsers.isEmpty()) {
      ArrayList<String> itemsIds = new ArrayList<>(favoriteUsers.keySet());
      addSuggestedfavoritesInteractor.addSuggestedFavorites(itemsIds, FollowableType.USER,
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

  public void putUserFavorite(OnBoardingModel onBoardingModel) {
    UserModel userModel = onBoardingModel.getUserModel();
    if (!favoriteUsers.containsKey(userModel.getIdUser())) {
      favoriteUsers.put(userModel.getIdUser(), userModel);
    }
  }

  public void removeFavorite(String idStream) {
    if (favoriteStreams.containsKey(idStream)) {
      favoriteStreams.remove(idStream);
    }
  }

  public void removeUserFavorite(String idStream) {
    if (favoriteUsers.containsKey(idStream)) {
      favoriteUsers.remove(idStream);
    }
  }

  private void checkStreamsLoaded() {
    onBoardingView.hideGetStarted();
    onBoardingView.goNextScreen();
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
