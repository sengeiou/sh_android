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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

public class GetMentionedPeopleInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository remoteUserRepository;
    private final UserRepository localUserRepository;
    private final SessionRepository sessionRepository;
    private String mention;
    private Callback<List<User>> callback;

    @Inject
    public GetMentionedPeopleInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Remote UserRepository remoteUserRepository, @Local UserRepository localUserRepository,
      SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteUserRepository = remoteUserRepository;
        this.localUserRepository = localUserRepository;
        this.sessionRepository = sessionRepository;
    }

    public void obtainMentionedPeople(String mention, Callback<List<User>> callback) {
        this.mention = mention;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        obtainLocalPeopleThenFallbackToRemote();
    }

    private void obtainLocalPeopleThenFallbackToRemote() {
        List<User> userList = localUserRepository.getLocalPeople(sessionRepository.getCurrentUserId());
        if (!userList.isEmpty()) {
            notifyResult(getUsersPossiblyMentioned(userList));
        } else {
            obtainRemotePeople();
        }
    }

    private void obtainRemotePeople() {
        try {
            List<User> userList = remoteUserRepository.getPeople();
            notifyResult(getUsersPossiblyMentioned(userList));
        } catch (ServerCommunicationException networkError) {
            /* no-op */
        }
    }

    private List<User> getUsersPossiblyMentioned(List<User> userList) {
        List<User> users = new ArrayList<>();
        for (User user : userList) {
            if (user.getUsername().toLowerCase().contains(mention.toLowerCase()) || user.getName()
              .toLowerCase()
              .contains(mention.toLowerCase())) {
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
