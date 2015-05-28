package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.User;
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

public class GetEventMediaCountInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShotRepository remoteShotRepository;
    private final ShotRepository localShotRepository;
    private final UserRepository remoteUserRepository;
    private final UserRepository localUserRepository;
    private final SessionRepository sessionRepository;

    private String idEvent;
    private String idUser;
    private Callback<Integer> callback;

    @Inject public GetEventMediaCountInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Remote ShotRepository remoteShotRepository,
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

    public void getEventMediaCount(String idEvent, Callback callback) {
        this.idEvent = idEvent;
        this.callback = callback;
        this.idUser = sessionRepository.getCurrentUserId();
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        getMediaCountFromLocal();
        getMediaCountFromRemote();
    }

    private void getMediaCountFromLocal() {
        List<User> people = localUserRepository.getPeople();
        List<String> peopleIds  = getPeopleInEvent(people);
        Integer mediaCountByIdEvent = getMediaCountFromRepository(peopleIds, localShotRepository);
        notifyLoaded(mediaCountByIdEvent);
    }

    private void getMediaCountFromRemote() {
        List<User> people = remoteUserRepository.getPeople();
        List<String> peopleIds  = getPeopleInEvent(people);
        Integer mediaCountByIdEvent = getMediaCountFromRepository(peopleIds, remoteShotRepository);
        notifyLoaded(mediaCountByIdEvent);
    }

    private Integer getMediaCountFromRepository(List<String> peopleIds, ShotRepository shotRepository) {
        Integer mediaCountByIdEvent = 0;
        for (String userId : peopleIds) {
            mediaCountByIdEvent += shotRepository.getMediaCountByIdEvent(idEvent, userId);
        }
        return mediaCountByIdEvent;
    }

    private List<String> getPeopleInEvent(List<User> people) {
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
}
