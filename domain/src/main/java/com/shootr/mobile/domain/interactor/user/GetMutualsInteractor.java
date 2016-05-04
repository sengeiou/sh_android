package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.FollowRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

public class GetMutualsInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository remoteUserRepository;
    private final UserRepository localUserRepository;
    private final FollowRepository localFollowRepository;

    private Callback<List<User>> callback;
    private ErrorCallback errorCallback;

    @Inject public GetMutualsInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Remote UserRepository remoteUserRepository, @Local UserRepository localUserRepository,
      @Local FollowRepository localFollowRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteUserRepository = remoteUserRepository;
        this.localUserRepository = localUserRepository;
        this.localFollowRepository = localFollowRepository;
    }

    public void obtainMutuals(Callback<List<User>> callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        List<String> mutuals = localFollowRepository.getMutualIdUsers();
        obtainMutuals(localUserRepository, mutuals);
        obtainRemoteMutuals(mutuals);
    }

    private void obtainRemoteMutuals(List<String> mutuals) {
        try {
            obtainMutuals(remoteUserRepository, mutuals);
        } catch (ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private void obtainMutuals(UserRepository userRepository, List<String> mutuals) {
        List<User> userList = userRepository.getPeople();
        List<User> mutualUsers = new ArrayList<>(mutuals.size());
        for (User user : userList) {
            if (mutuals.contains(user.getIdUser())) {
                mutualUsers.add(user);
            }
        }
        notifyResult(reorderUsersByUsername(mutualUsers));
    }

    private List<User> reorderUsersByUsername(List<User> userList) {
        if (userList != null) {
            Collections.sort(userList, new UsernameComparator());
            return userList;
        } else {
            return Collections.emptyList();
        }
    }

    private void notifyResult(final List<User> userList) {
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

    static class UsernameComparator implements Comparator<User> {

        @Override public int compare(User user1, User user2) {
            return user1.getUsername().compareToIgnoreCase(user2.getUsername());
        }
    }
}
