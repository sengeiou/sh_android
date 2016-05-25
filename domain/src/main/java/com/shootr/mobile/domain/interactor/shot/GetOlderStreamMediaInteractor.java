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
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.ShotRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetOlderStreamMediaInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ShotRepository remoteShotRepository;
  private final UserRepository remoteUserRepository;
  private final SessionRepository sessionRepository;
  private ErrorCallback errorCallback;

  private String idStream;
  private Interactor.Callback<List<Shot>> callback;
  private String currentidUser;
  private Long maxTimestamp;

  @Inject public GetOlderStreamMediaInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Remote ShotRepository remoteShotRepository,
      @Remote UserRepository remoteUserRepository, SessionRepository sessionRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remoteShotRepository = remoteShotRepository;
    this.remoteUserRepository = remoteUserRepository;
    this.sessionRepository = sessionRepository;
  }

  public void getOlderStreamMedia(String idStream, Long maxTimestamp, Callback<List<Shot>> callback,
      ErrorCallback errorCallback) {
    this.idStream = idStream;
    this.maxTimestamp = maxTimestamp;
    this.callback = callback;
    this.errorCallback = errorCallback;
    this.currentidUser = sessionRepository.getCurrentUserId();
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      getMediaFromRemote();
    } catch (ServerCommunicationException error) {
      notifyError(error);
    }
  }

  private void getMediaFromRemote() {
    List<User> people = remoteUserRepository.getPeople();
    List<String> peopleIds = getPeopleInStream(people);
    List<Shot> shots = remoteShotRepository.getMediaByIdStream(idStream, peopleIds, maxTimestamp,
        StreamMode.TYPES_STREAM, ShotType.TYPES_SHOWN);
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
      @Override public void run() {
        errorCallback.onError(error);
      }
    });
  }
}