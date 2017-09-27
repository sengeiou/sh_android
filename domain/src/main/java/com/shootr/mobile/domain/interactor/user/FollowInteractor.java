package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.FollowingBlockedUserException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.user.SuggestedPeople;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.follow.FollowRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import com.shootr.mobile.domain.service.user.CannotFollowBlockedUserException;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class FollowInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final FollowRepository localFollowRepository;
  private final FollowRepository remoteFollowRepository;
  private final UserRepository remoteUserRepository;
  private final UserRepository localUserRepository;
  private final SessionRepository sessionRepository;

  private String idUser;
  private CompletedCallback callback;
  private ErrorCallback errorCallback;

  @Inject public FollowInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local FollowRepository localFollowRepository,
      @Remote FollowRepository remoteFollowRepository, @Remote UserRepository remoteUserRepository,
      @Local UserRepository localUserRepository, SessionRepository sessionRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localFollowRepository = localFollowRepository;
    this.remoteFollowRepository = remoteFollowRepository;
    this.remoteUserRepository = remoteUserRepository;
    this.localUserRepository = localUserRepository;
    this.sessionRepository = sessionRepository;
  }

  public void follow(String idUser, CompletedCallback callback, ErrorCallback errorCallback) {
    this.idUser = checkNotNull(idUser);
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    if (!sessionRepository.getCurrentUserId().equals(idUser)) {
      localFollowRepository.follow(idUser);
      try {
        remoteFollowRepository.follow(idUser);
        ensureUserExistInLocal();
        updateSuggestedPeopleCache();
        notifyCompleted();
      } catch (FollowingBlockedUserException error) {
        notifyError(new CannotFollowBlockedUserException(error));
      }
    }
  }

  private void updateSuggestedPeopleCache() {
    List<SuggestedPeople> localSuggestions = localUserRepository.getSuggestedPeople(null);
    for (int i = 0; i < localSuggestions.size(); i++) {
      SuggestedPeople suggestedPeople = localSuggestions.get(i);
      if (suggestedPeople.getUser().getIdUser().equals(idUser)) {
        suggestedPeople.getUser().setFollowing(true);
        localSuggestions.set(i, suggestedPeople);
        break;
      }
    }
    localUserRepository.updateSuggestedPeopleCache(localSuggestions);
  }

  protected void ensureUserExistInLocal() {
    try {
      if (localUserRepository.getUserById(idUser) == null) {
        User user = remoteUserRepository.getUserById(idUser);
        localUserRepository.putUser(user);
      }
    } catch (ServerCommunicationException e) {
            /* bad luck: will have unconsistent data for a short period of time */
    }
  }

  private void notifyCompleted() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onCompleted();
      }
    });
  }

  private void notifyError(final ShootrException error) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        errorCallback.onError(error);
      }
    });
  }
}
