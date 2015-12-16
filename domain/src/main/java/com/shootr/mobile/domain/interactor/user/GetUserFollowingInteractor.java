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

public class GetUserFollowingInteractor implements Interactor{

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository remoteUserRepository;
    private final UserRepository localUserRepository;
    private final SessionRepository sessionRepository;

    private String idUser;
    private Callback<List<User>> callback;
    private ErrorCallback errorCallback;

    @Inject public GetUserFollowingInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Remote UserRepository remoteUserRepository,
      @Local UserRepository localUserRepository, SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteUserRepository = remoteUserRepository;
        this.localUserRepository = localUserRepository;
        this.sessionRepository = sessionRepository;
    }

    public void obtainFollowing(String idUser, Callback<List<User>> callback, ErrorCallback errorCallback) {
        this.idUser = idUser;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        obtainFollowing();
    }

    private void obtainFollowing() {
        try {
            List<User> following = remoteUserRepository.getFollowing(idUser);
            String currentUserId = sessionRepository.getCurrentUserId();
            List<User> currentUserFollowing = localUserRepository.getPeople();
            List<User> followingWithRelationship = setRelationshipInUsers(following, currentUserId, currentUserFollowing);
            notifyResult(followingWithRelationship);
        } catch (ServerCommunicationException networkError) {
            notifyError(networkError);
        }
    }

    private List<User> setRelationshipInUsers(List<User> usersFollowers, String currentUserId,
      List<User> currentUserFollowing) {
        List<User> users = new ArrayList<>();
        for(User user: usersFollowers){
            String idUser = user.getIdUser();
            boolean isMe = idUser.equals(currentUserId);
            if (isMe) {
                user.setRelationship(1);
            } else if (currentUserFollowing.contains(user)) {
                user.setRelationship(2);
            } else {
                user.setRelationship(0);
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
