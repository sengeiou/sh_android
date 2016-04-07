package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

public class ReactiveSearchPeopleInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository localUserRepository;
    private final SessionRepository sessionRepository;
    private String query;
    private Interactor.Callback<List<User>> callback;

    @Inject
    public ReactiveSearchPeopleInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local UserRepository localUserRepository, SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localUserRepository = localUserRepository;
        this.sessionRepository = sessionRepository;
    }

    public void obtainPeople(String query, Callback<List<User>> callback) {
        this.query = query;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        obtainLocalPeople();
    }

    private void obtainLocalPeople() {
        List<User> userList = localUserRepository.getLocalPeople(sessionRepository.getCurrentUserId());
        notifyResult(getUsersPossiblyMentioned(userList));
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
