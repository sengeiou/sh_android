package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.model.user.Contributor;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.ContributorRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetUserCanPinMessageInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final ContributorRepository localContributorRepository;
  private final ContributorRepository remoteContributorRepository;
  private final SessionRepository sessionRepository;
  private final PostExecutionThread postExecutionThread;

  private Callback<Boolean> callback;
  private String idStream;
  private String streamAuthorIdUser;

  @Inject public GetUserCanPinMessageInteractor(InteractorHandler interactorHandler,
      @Local ContributorRepository localContributorRepository,
      @Remote ContributorRepository remoteContributorRepository,
      SessionRepository sessionRepository, PostExecutionThread postExecutionThread) {
    this.interactorHandler = interactorHandler;
    this.localContributorRepository = localContributorRepository;
    this.remoteContributorRepository = remoteContributorRepository;
    this.sessionRepository = sessionRepository;
    this.postExecutionThread = postExecutionThread;
  }

  public void canUserPinMessage(String idStream, String streamAuthorIdUser,
      Callback<Boolean> callback) {
    this.idStream = idStream;
    this.streamAuthorIdUser = streamAuthorIdUser;
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      String currentUserId = sessionRepository.getCurrentUserId();
      boolean isUserHolder = currentUserId.equals(streamAuthorIdUser);
      List<String> idUsersContributing = getContributors(localContributorRepository);
      if (idUsersContributing.isEmpty()) {
        idUsersContributing = getContributors(remoteContributorRepository);
      }
      boolean isUserContributor = idUsersContributing.contains(currentUserId);
      notifyResult(isUserHolder || isUserContributor);
    } catch (ServerCommunicationException error) {
      /* no-op */
    }
  }

  private List<String> getContributors(ContributorRepository contributorRepository) {
    List<Contributor> contributors = contributorRepository.getContributors(idStream);
    List<String> idUsersContributing = new ArrayList<>(contributors.size());
    for (Contributor contributor : contributors) {
      idUsersContributing.add(contributor.getIdUser());
    }
    return idUsersContributing;
  }

  private void notifyResult(final Boolean isContributor) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(isContributor);
      }
    });
  }
}
