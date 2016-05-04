package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
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
    private final FollowRepository remoteFollowRepository;

    private Callback<List<User>> callback;

    @Inject
    public GetMutualsInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Remote UserRepository remoteUserRepository, @Local UserRepository localUserRepository,
      @Local FollowRepository localFollowRepository, @Remote FollowRepository remoteFollowRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteUserRepository = remoteUserRepository;
        this.localUserRepository = localUserRepository;
        this.localFollowRepository = localFollowRepository;
        this.remoteFollowRepository = remoteFollowRepository;
    }

    public void obtainMutuals(Callback<List<User>> callback) {
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        obtainRemoteMutuals();
    }

    private void obtainLocalMutuals() {
        List<String> mutuals = localFollowRepository.getMutualIdUsers();
        obtainMutuals(localUserRepository, mutuals);
    }

    private void obtainRemoteMutuals() {
        try {
            List<String> mutuals = remoteFollowRepository.getMutualIdUsers();
            obtainMutuals(remoteUserRepository, mutuals);
        } catch (ServerCommunicationException error) {
            obtainLocalMutuals();
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

    static class UsernameComparator implements Comparator<User> {

        @Override public int compare(User user1, User user2) {
            return user1.getUsername().compareToIgnoreCase(user2.getUsername());
        }
    }
}
