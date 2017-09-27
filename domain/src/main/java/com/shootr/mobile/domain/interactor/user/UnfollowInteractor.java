package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.user.SuggestedPeople;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.follow.FollowRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import java.util.List;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class UnfollowInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final FollowRepository localFollowRepository;
  private final FollowRepository remoteFollowRepository;
  private final UserRepository localUserRepository;
  private final UserRepository remoteUserRepository;
  private final SessionRepository sessionRepository;

  private String idUser;
  private CompletedCallback callback;

  @Inject public UnfollowInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local FollowRepository localFollowRepository,
      @Remote FollowRepository remoteFollowRepository, @Local UserRepository localUserRepository,
      @Remote UserRepository remoteUserRepository, SessionRepository sessionRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localFollowRepository = localFollowRepository;
    this.remoteFollowRepository = remoteFollowRepository;
    this.localUserRepository = localUserRepository;
    this.remoteUserRepository = remoteUserRepository;
    this.sessionRepository = sessionRepository;
  }

  public void unfollow(String idUser, CompletedCallback callback) {
    this.idUser = checkNotNull(idUser);
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    if (!sessionRepository.getCurrentUserId().equals(idUser)) {
      localFollowRepository.unfollow(idUser);
      remoteFollowRepository.unfollow(idUser);
      updateUserInLocal();
      updateSuggestedPeopleCache();
      notifyCompleted();
    }
  }

  private void updateSuggestedPeopleCache() {
    List<SuggestedPeople> localSuggestions = localUserRepository.getSuggestedPeople(null);
    for (int i = 0; i < localSuggestions.size(); i++) {
      SuggestedPeople suggestedPeople = localSuggestions.get(i);
      if (suggestedPeople.getUser().getIdUser().equals(idUser)) {
        suggestedPeople.getUser().setFollowing(false);
        localSuggestions.set(i, suggestedPeople);
        break;
      }
    }
    localUserRepository.updateSuggestedPeopleCache(localSuggestions);
  }

  protected void updateUserInLocal() {
    try {
      remoteUserRepository.getUserById(idUser);
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
}
