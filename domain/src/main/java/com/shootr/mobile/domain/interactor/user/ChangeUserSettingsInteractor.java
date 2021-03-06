package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.user.UserSettings;
import com.shootr.mobile.domain.repository.user.UserSettingsRepository;
import javax.inject.Inject;

public class ChangeUserSettingsInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final UserSettingsRepository externalUserSettingsRepository;
  private CompletedCallback callback;
  private ErrorCallback errorCallback;
  private UserSettings userSettings;
  private String pushSettingsType;

  @Inject public ChangeUserSettingsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      UserSettingsRepository externalUserSettingsRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.externalUserSettingsRepository = externalUserSettingsRepository;
  }

  public void changeSettings(UserSettings userSettings, String pushSettingsType,
      CompletedCallback callback, ErrorCallback errorCallback) {
    this.callback = callback;
    this.errorCallback = errorCallback;
    this.userSettings = userSettings;
    this.pushSettingsType = pushSettingsType;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      externalUserSettingsRepository.modifyUserSettings(userSettings, pushSettingsType);
      notifyResult();
    } catch (ServerCommunicationException error) {
      notifyError(error);
    }
  }

  private void notifyResult() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onCompleted();
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

