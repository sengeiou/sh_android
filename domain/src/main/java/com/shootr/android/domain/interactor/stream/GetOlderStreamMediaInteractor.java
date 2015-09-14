package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetOlderStreamMediaInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShotRepository remoteShotRepository;
    private final UserRepository remoteUserRepository;
    private final SessionRepository sessionRepository;
    private Interactor.ErrorCallback errorCallback;

    private String idStream;
    private Interactor.Callback<List<Shot>> callback;
    private String currentidUser;
    private Long maxTimestamp;

    @Inject
    public GetOlderStreamMediaInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Remote ShotRepository remoteShotRepository,
      @Remote UserRepository remoteUserRepository,
      SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteShotRepository = remoteShotRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.sessionRepository = sessionRepository;
    }

    public void getOlderStreamMedia(String idStream, Long maxTimestamp, Interactor.Callback<List<Shot>> callback, Interactor.ErrorCallback errorCallback) {
        this.idStream = idStream;
        this.maxTimestamp = maxTimestamp;
        this.callback = callback;
        this.errorCallback = errorCallback;
        this.currentidUser = sessionRepository.getCurrentUserId();
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try{
            getMediaFromRemote();
        } catch (ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private void getMediaFromRemote() {
        List<User> people = remoteUserRepository.getPeople();
        List<String> peopleIds = getPeopleInStream(people);
        List<Shot> shots = remoteShotRepository.getMediaByIdStream(idStream, peopleIds, maxTimestamp);
        notifyLoaded(shots);
    }

    private List<String> getPeopleInStream(List<User> people) {
        List<String> peopleIds = new ArrayList<>();
        for (User user : people) {
            peopleIds.add(user.getIdUser());
        }
        peopleIds.add(currentidUser);
        return peopleIds;
    }

    private void notifyLoaded(final List<Shot> shots) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(shots);
            }
        });
    }

    private void notifyError(final ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                errorCallback.onError(error);
            }
        });
    }
}