package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetUserFollowersInteractor implements Interactor {

    public static final int PAGE_SIZE = 50;
    public static final int IS_ME = 1;
    public static final int FOLLOWING = 2;
    public static final int NO_RELATIONSHIP = 0;
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository remoteUserRepository;
    private final UserRepository localUserRepository;
    private final SessionRepository sessionRepository;

    private String idUser;
    private Callback<List<User>> callback;
    private ErrorCallback errorCallback;
    private Integer page;

    @Inject
    public GetUserFollowersInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Remote UserRepository remoteUserRepository, @Local UserRepository localUserRepository,
      SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteUserRepository = remoteUserRepository;
        this.localUserRepository = localUserRepository;
        this.sessionRepository = sessionRepository;
    }

    public void obtainFollowers(String idUser, Integer page, Callback<List<User>> callback,
      ErrorCallback errorCallback) {
        this.idUser = idUser;
        this.page = page;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        obtainFollowers();
    }

    private void obtainFollowers() {
        try {
            List<User> usersFollowers = remoteUserRepository.getFollowers(idUser, page, PAGE_SIZE);
            String currentUserId = sessionRepository.getCurrentUserId();
            List<User> currentUserFollowing = localUserRepository.getPeople();
            List<User> followers = setRelationshipInUsers(usersFollowers, currentUserId, currentUserFollowing);
            notifyResult(followers);
        } catch (ServerCommunicationException networkError) {
            notifyError(networkError);
        }
    }

    private List<User> setRelationshipInUsers(List<User> usersFollowers, String currentUserId,
      List<User> currentUserFollowing) {
        List<User> users = new ArrayList<>();
        for (User user : usersFollowers) {
            String idUser = user.getIdUser();
            boolean isMe = idUser.equals(currentUserId);
            if (isMe) {
                user.setRelationship(IS_ME);
            } else if (currentUserFollowing.contains(user)) {
                user.setRelationship(FOLLOWING);
            } else {
                user.setRelationship(NO_RELATIONSHIP);
            }
            users.add(user);
        }
        return users;
    }

    private void notifyResult(final List<User> following) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(following);
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
