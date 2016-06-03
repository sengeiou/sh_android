package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.ShotType;
import com.shootr.mobile.domain.StreamMode;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.ShotRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetStreamMediaInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ShotRepository remoteShotRepository;
  private final ShotRepository localShotRepository;
  private final UserRepository remoteUserRepository;
  private final UserRepository localUserRepository;
  private final SessionRepository sessionRepository;
  private ErrorCallback errorCallback;

  private String idStream;
  private Callback<List<Shot>> callback;
  private String currentidUser;

  @Inject public GetStreamMediaInteractor(InteractorHandler interactorHandler,
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

  public void getStreamMedia(String idStream, Callback<List<Shot>> callback,
      ErrorCallback errorCallback) {
    this.idStream = idStream;
    this.callback = callback;
    this.errorCallback = errorCallback;
    this.currentidUser = sessionRepository.getCurrentUserId();
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      getMediaFromLocal();
      getMediaFromRemote();
    } catch (ServerCommunicationException error) {
      notifyError(error);
    }
  }

  private void getMediaFromLocal() {
    List<User> people = localUserRepository.getPeople();
    List<String> peopleIds = getPeopleInStream(people);
    List<Shot> shots = getShotsFromRepository(peopleIds, localShotRepository, Long.MAX_VALUE);
    notifyLoaded(shots);
  }

  private void getMediaFromRemote() {
    List<User> people = remoteUserRepository.getPeople();
    List<String> peopleIds = getPeopleInStream(people);
    List<Shot> shots = getShotsFromRepository(peopleIds, remoteShotRepository, Long.MAX_VALUE);
    notifyLoaded(shots);
  }

  private List<Shot> getShotsFromRepository(List<String> peopleIds, ShotRepository shotRepository,
      Long maxTimestamp) {
    return shotRepository.getMediaByIdStream(idStream, peopleIds, maxTimestamp,
        StreamMode.TYPES_STREAM, ShotType.TYPES_SHOWN);
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
      @Override public void run() {
        errorCallback.onError(error);
      }
    });
  }
}
