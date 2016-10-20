package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.UserAlreadyCheckInRequestException;
import com.shootr.mobile.domain.exception.UserCannotCheckInRequestException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import com.shootr.mobile.domain.service.user.UserAlreadyCheckedException;
import com.shootr.mobile.domain.service.user.UserCannotCheckInException;
import javax.inject.Inject;

public class CallCtaCheckInInteractor implements Interactor {

  private final ExternalShotRepository externalShotRepository;
  private final PostExecutionThread postExecutionThread;
  private final InteractorHandler interactorHandler;
  private ErrorCallback errorCallback;
  private String idStream;
  private Interactor.CompletedCallback completedCallback;

  @Inject public CallCtaCheckInInteractor(ExternalShotRepository externalShotRepository,
      PostExecutionThread postExecutionThread, InteractorHandler interactorHandler) {
    this.externalShotRepository = externalShotRepository;
    this.postExecutionThread = postExecutionThread;
    this.interactorHandler = interactorHandler;
  }

  public void checkIn(String idStream, Interactor.CompletedCallback completedCallback, ErrorCallback errorCallback) {
    this.idStream = idStream;
    this.completedCallback = completedCallback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      if (checkInStream()) {
        notifyLoaded();
      }
    } catch (ServerCommunicationException error) {
      notifyError(error);
    }
  }

  private boolean checkInStream() {
    try {
      externalShotRepository.callCtaCheckIn(idStream);
      return true;
    } catch (UserAlreadyCheckInRequestException e) {
      notifyError(new UserAlreadyCheckedException(e));
      return false;
    } catch (UserCannotCheckInRequestException e) {
      notifyError(new UserCannotCheckInException(e));
      return false;
    }
  }

  private void notifyLoaded() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        completedCallback.onCompleted();
      }
    });
  }

  protected void notifyError(final ShootrException error) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        errorCallback.onError(error);
      }
    });
  }
}
