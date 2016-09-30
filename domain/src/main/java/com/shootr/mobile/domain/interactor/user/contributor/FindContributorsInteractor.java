package com.shootr.mobile.domain.interactor.user.contributor;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.user.Contributor;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.ContributorRepository;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.user.UserRepository;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class FindContributorsInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final ContributorRepository contributorRepository;
  private final UserRepository remoteUserRepository;
  private final PostExecutionThread postExecutionThread;
  private final LocaleProvider localeProvider;

  private Callback<List<User>> callback;
  private ErrorCallback errorCallback;
  private String query;
  private Integer currentPage;
  private String idStream;

  @Inject public FindContributorsInteractor(InteractorHandler interactorHandler,
      @Remote ContributorRepository contributorRepository, PostExecutionThread postExecutionThread,
      @Remote UserRepository remoteUserRepository, LocaleProvider localeProvider) {
    this.interactorHandler = interactorHandler;
    this.contributorRepository = contributorRepository;
    this.remoteUserRepository = remoteUserRepository;
    this.postExecutionThread = postExecutionThread;
    this.localeProvider = localeProvider;
  }

  public void findContributors(String idStream, String query, Integer currentPage,
      Callback<List<User>> callback, ErrorCallback errorCallback) {
    this.idStream = idStream;
    this.query = query;
    this.currentPage = currentPage;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    notifyLoaded(findRemoteContributors());
  }

  private List<User> findRemoteContributors() throws IOException {
    return findNotAddedContributors();
  }

  private List<User> findNotAddedContributors() throws IOException {
    List<User> resultUsers = new ArrayList<>();
    try {
      List<User> users =
          remoteUserRepository.findFriends(query, currentPage, localeProvider.getLocale());
      List<Contributor> currentContributors =
          contributorRepository.getContributorsWithUsers(idStream);
      filterContributors(resultUsers, users, currentContributors);
    } catch (ServerCommunicationException error) {
      notifyError(error);
    }
    return resultUsers;
  }

  private void filterContributors(List<User> resultUsers, List<User> users,
      List<Contributor> currentContributors) {
    List<User> contributorUsers = new ArrayList<>(currentContributors.size());

    for (Contributor currentContributor : currentContributors) {
      contributorUsers.add(currentContributor.getUser());
    }

    for (User user : users) {
      if (!contributorUsers.contains(user)) {
        resultUsers.add(user);
      }
    }
  }

  private void notifyLoaded(final List<User> results) {
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
