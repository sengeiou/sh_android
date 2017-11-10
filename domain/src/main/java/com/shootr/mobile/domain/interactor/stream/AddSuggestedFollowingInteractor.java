package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.onBoarding.ExternalOnBoardingRepository;
import com.shootr.mobile.domain.service.StreamIsAlreadyInFavoritesException;
import java.util.List;
import javax.inject.Inject;

public class AddSuggestedFollowingInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ExternalOnBoardingRepository remoteFavoriteRepository;

  private Interactor.CompletedCallback completedCallback;
  private ErrorCallback errorCallback;

  private List<String> idOnBoardings;
  private String type;

  @Inject public AddSuggestedFollowingInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      ExternalOnBoardingRepository remoteFavoriteRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remoteFavoriteRepository = remoteFavoriteRepository;
  }

  public void addSuggestedFollowing(List<String> idOnBoardings, String type, CompletedCallback completedCallback,
      ErrorCallback errorCallback) {
    this.completedCallback = completedCallback;
    this.errorCallback = errorCallback;
    this.idOnBoardings = idOnBoardings;
    this.type = type;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      remoteFavoriteRepository.addSuggestedFavorites(idOnBoardings, type);
      notifyLoaded();
    } catch (ServerCommunicationException error) {
      notifyError(new StreamIsAlreadyInFavoritesException(error));
    }
  }

  protected void notifyLoaded() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        completedCallback.onCompleted();
      }
    });
  }

  private void notifyError(final ShootrException e) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        errorCallback.onError(e);
      }
    });
  }
}
