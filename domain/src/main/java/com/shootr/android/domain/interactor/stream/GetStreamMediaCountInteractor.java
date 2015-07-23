package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetStreamMediaCountInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShotRepository remoteShotRepository;
    private final ShotRepository localShotRepository;
    private final UserRepository remoteUserRepository;
    private final UserRepository localUserRepository;
    private final SessionRepository sessionRepository;
    private ErrorCallback errorCallback;

    private String idStream;
    private String idUser;
    private Callback<Integer> callback;

    @Inject public GetStreamMediaCountInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Remote ShotRepository remoteShotRepository,
      @Local ShotRepository localShotRepository, @Remote UserRepository remoteUserRepository,
      @Local UserRepository localUserRepository, SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteShotRepository = remoteShotRepository;
        this.localShotRepository = localShotRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.localUserRepository = localUserRepository;
        this.sessionRepository = sessionRepository;
    }

    public void getStreamMediaCount(String idStream, Callback callback, ErrorCallback errorCallback) {
        this.idStream = idStream;
        this.callback = callback;
        this.errorCallback = errorCallback;
        this.idUser = sessionRepository.getCurrentUserId();
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            getMediaCountFromLocal();
            getMediaCountFromRemote();
        } catch (ServerCommunicationException error) {
            notifyError(error);
        }

    }

    private void getMediaCountFromLocal() {
        List<User> people = localUserRepository.getPeople();
        List<String> peopleIds  = getPeopleInStream(people);
        Integer mediaCountByIdStream = getMediaCountFromRepository(peopleIds, localShotRepository);
        notifyLoaded(mediaCountByIdStream);
    }

    private void getMediaCountFromRemote() {
        List<User> people = remoteUserRepository.getPeople();
        List<String> peopleIds  = getPeopleInStream(people);
        Integer mediaCountByIdStream = getMediaCountFromRepository(peopleIds, remoteShotRepository);
        notifyLoaded(mediaCountByIdStream);
    }

    private Integer getMediaCountFromRepository(List<String> peopleIds, ShotRepository shotRepository) {
        return shotRepository.getMediaCountByIdStream(idStream, peopleIds);
    }

    private List<String> getPeopleInStream(List<User> people) {
        List<String> peopleIds = new ArrayList<>();
        for (User user : people) {
            peopleIds.add(user.getIdUser());
        }
        peopleIds.add(idUser);
        return peopleIds;
    }

    private void notifyLoaded(final Integer mediaCount) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(mediaCount);
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
