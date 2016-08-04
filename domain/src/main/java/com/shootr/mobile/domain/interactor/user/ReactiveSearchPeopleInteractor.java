package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

public class ReactiveSearchPeopleInteractor implements Interactor {

  public static final int PAGE_OFFSET = 0;
  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final UserRepository localUserRepository;
  private final UserRepository remoteUserRepository;
  private final SessionRepository sessionRepository;
  private final LocaleProvider localeProvider;
  private String query;
  private Interactor.Callback<List<User>> callback;

  @Inject public ReactiveSearchPeopleInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local UserRepository localUserRepository,
      @Remote UserRepository remoteUserRepository, SessionRepository sessionRepository,
      LocaleProvider localeProvider) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localUserRepository = localUserRepository;
    this.remoteUserRepository = remoteUserRepository;
    this.sessionRepository = sessionRepository;
    this.localeProvider = localeProvider;
  }

  public void obtainPeople(String query, Callback<List<User>> callback) {
    this.query = query;
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      List<User> userList = remoteUserRepository.findFriends(query, PAGE_OFFSET, localeProvider.getLocale());
      notifyResult(getUsersPossiblyMentioned(userList));
    } catch (ServerCommunicationException e) {
      obtainLocalPeople();
    }
  }

  private void obtainLocalPeople() {
    List<User> userList = localUserRepository.getLocalPeople(sessionRepository.getCurrentUserId());
    if (!userList.isEmpty()) {
      notifyResult(getUsersPossiblyMentioned(userList));
    }
  }

  private List<User> getUsersPossiblyMentioned(List<User> userList) {
    List<User> users = new ArrayList<>();
    for (User user : userList) {
      if (user.getUsername().toLowerCase().contains(query.toLowerCase()) || user.getName()
          .toLowerCase()
          .contains(query.toLowerCase())) {
        users.add(user);
      }
    }
    return reorderPeopleByUsername(users);
  }

  private List<User> reorderPeopleByUsername(List<User> userList) {
    if (userList != null) {
      Collections.sort(userList, new UsernameComparator());
      return userList;
    } else {
      return Collections.emptyList();
    }
  }

  private void notifyResult(final List<User> suggestedPeople) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(suggestedPeople);
      }
    });
  }

  static class UsernameComparator implements Comparator<User> {

    @Override public int compare(User user1, User user2) {
      return user1.getUsername().compareToIgnoreCase(user2.getUsername());
    }
  }
}
