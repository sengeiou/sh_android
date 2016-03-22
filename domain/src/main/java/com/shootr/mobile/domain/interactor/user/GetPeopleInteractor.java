package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.UserList;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

public class GetPeopleInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository remoteUserRepository;
    private final UserRepository localUserRepository;
    private Callback<UserList> callback;
    private ErrorCallback errorCallback;

    @Inject public GetPeopleInteractor(InteractorHandler interactorHandler, @Remote UserRepository remoteUserRepository,
      @Local UserRepository localUserRepository, PostExecutionThread postExecutionThread) {
        this.interactorHandler = interactorHandler;
        this.remoteUserRepository = remoteUserRepository;
        this.localUserRepository = localUserRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void obtainPeople(Callback<UserList> callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        obtainLocalPeople();
        obtainRemotePeople();
    }

    private void obtainLocalPeople() {
        List<User> userList = localUserRepository.getPeople();
        if (!userList.isEmpty()) {
            userList = reorderPeopleByUsername(userList);
            notifyLoaded(new UserList(userList));
        }
    }

    private void obtainRemotePeople() {
        try {
            List<User> userList = remoteUserRepository.getPeople();
            userList = reorderPeopleByUsername(userList);
            notifyLoaded(new UserList(userList));
        } catch (ServerCommunicationException networkError) {
            notifyError(networkError);
        }
    }

    private List<User> reorderPeopleByUsername(List<User> userList) {
        if (userList != null) {
            Collections.sort(userList, new UsernameComparator());
            return userList;
        } else {
            return Collections.emptyList();
        }
    }

    static class UsernameComparator implements Comparator<User> {

        @Override public int compare(User user1, User user2) {
            return user1.getUsername().compareToIgnoreCase(user2.getUsername());
        }
    }

    private void notifyLoaded(final UserList userList) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(userList);
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
