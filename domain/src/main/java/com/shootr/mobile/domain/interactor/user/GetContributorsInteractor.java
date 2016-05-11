package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.Contributor;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.ContributorRepository;
import com.shootr.mobile.domain.repository.Remote;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetContributorsInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final ContributorRepository contributorRepository;
  private final PostExecutionThread postExecutionThread;

  private Callback<List<Contributor>> callback;
  private ErrorCallback errorCallback;
  private String idStream;
  private Boolean withUsersEmbed;

  @Inject public GetContributorsInteractor(InteractorHandler interactorHandler,
      @Remote ContributorRepository contributorRepository,
      PostExecutionThread postExecutionThread) {
    this.interactorHandler = interactorHandler;
    this.contributorRepository = contributorRepository;
    this.postExecutionThread = postExecutionThread;
  }

  public void obtainContributors(String idStream, Boolean withUsersEmbed,
      Callback<List<Contributor>> callback, ErrorCallback errorCallback) {
    this.idStream = idStream;
    this.withUsersEmbed = withUsersEmbed;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      notifyLoaded(obtainRemoteContributors());
    } catch (ServerCommunicationException error) {
      notifyError(error);
    }
  }

  private List<Contributor> obtainRemoteContributors() {
    if (withUsersEmbed) {
      return orderContributors(contributorRepository.getContributorsWithUsers(idStream));
    } else {
      return contributorRepository.getContributors(idStream);
    }
  }

  private List<Contributor> orderContributors(List<Contributor> contributors) {
    Collections.sort(contributors, new Contributor.AlphabeticContributorComparator());
    return contributors;
  }

  private void notifyLoaded(final List<Contributor> results) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(results);
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
