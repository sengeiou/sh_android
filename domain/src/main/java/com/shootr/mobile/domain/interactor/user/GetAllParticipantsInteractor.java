package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

public class GetAllParticipantsInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final UserRepository remoteUserRepository;
    private final PostExecutionThread postExecutionThread;

    private Callback<List<User>> callback;
    private ErrorCallback errorCallback;
    private String idStream;
    private Long timestamp;

    private Boolean isPaginating;

    @Inject public GetAllParticipantsInteractor(InteractorHandler interactorHandler,
      @Remote UserRepository remoteUserRepository, PostExecutionThread postExecutionThread) {
        this.interactorHandler = interactorHandler;
        this.remoteUserRepository = remoteUserRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void obtainAllParticipants(String idStream, Long timestamp, Boolean isPaginating,
      Callback<List<User>> callback, ErrorCallback errorCallback) {
        this.idStream = idStream;
        this.timestamp = timestamp;
        this.isPaginating = isPaginating;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            List<User> allParticipants = obtainAllParticipantsList();
            notifyLoaded(allParticipants);
        } catch (ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private List<User> obtainAllParticipantsList() {
        List<User> following = remoteUserRepository.getPeople();
        List<User> followingInStream = retainFollowingInThisStream(following);

        List<User> participants = remoteUserRepository.getAllParticipants(idStream, timestamp);

        participants.removeAll(followingInStream);

        List<User> allParticipants = new ArrayList<>();
        if (!isPaginating) {
            allParticipants.addAll(sortUserListByJoinStreamDate(followingInStream));
        }
        allParticipants.addAll(sortUserListByJoinStreamDate(participants));
        return allParticipants;
    }

    private List<User> retainFollowingInThisStream(List<User> following) {
        List<User> followingInStream = new ArrayList<>();

        for (User user : following) {
            if (user.getIdWatchingStream() != null && user.getIdWatchingStream().equals(idStream)) {
                followingInStream.add(user);
            }
        }
        return followingInStream;
    }

    private List<User> sortUserListByJoinStreamDate(List<User> watchesFromPeople) {
        Collections.sort(watchesFromPeople, new Comparator<User>() {
            @Override public int compare(User userModel, User t1) {
                return t1.getJoinStreamDate().compareTo(userModel.getJoinStreamDate());
            }
        });
        return watchesFromPeople;
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
