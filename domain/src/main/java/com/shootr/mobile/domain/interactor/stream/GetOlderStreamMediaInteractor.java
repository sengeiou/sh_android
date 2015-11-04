package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetOlderStreamMediaInteractor implements Interactor {

    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.repository.ShotRepository remoteShotRepository;
    private final UserRepository remoteUserRepository;
    private final com.shootr.mobile.domain.repository.SessionRepository sessionRepository;
    private Interactor.ErrorCallback errorCallback;

    private String idStream;
    private Interactor.Callback<List<com.shootr.mobile.domain.Shot>> callback;
    private String currentidUser;
    private Long maxTimestamp;

    @Inject
    public GetOlderStreamMediaInteractor(com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler,
      com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread,
      @com.shootr.mobile.domain.repository.Remote
      com.shootr.mobile.domain.repository.ShotRepository remoteShotRepository,
      @com.shootr.mobile.domain.repository.Remote UserRepository remoteUserRepository,
      com.shootr.mobile.domain.repository.SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteShotRepository = remoteShotRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.sessionRepository = sessionRepository;
    }

    public void getOlderStreamMedia(String idStream, Long maxTimestamp, Interactor.Callback<List<com.shootr.mobile.domain.Shot>> callback, Interactor.ErrorCallback errorCallback) {
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
        } catch (com.shootr.mobile.domain.exception.ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private void getMediaFromRemote() {
        List<com.shootr.mobile.domain.User> people = remoteUserRepository.getPeople();
        List<String> peopleIds = getPeopleInStream(people);
        List<com.shootr.mobile.domain.Shot> shots = remoteShotRepository.getMediaByIdStream(idStream, peopleIds, maxTimestamp);
        notifyLoaded(shots);
    }

    private List<String> getPeopleInStream(List<com.shootr.mobile.domain.User> people) {
        List<String> peopleIds = new ArrayList<>();
        for (com.shootr.mobile.domain.User user : people) {
            peopleIds.add(user.getIdUser());
        }
        peopleIds.add(currentidUser);
        return peopleIds;
    }

    private void notifyLoaded(final List<com.shootr.mobile.domain.Shot> shots) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(shots);
            }
        });
    }

    private void notifyError(final com.shootr.mobile.domain.exception.ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                errorCallback.onError(error);
            }
        });
    }
}