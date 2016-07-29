package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.user.UserSettings;
import com.shootr.mobile.domain.repository.user.UserSettingsRepository;
import javax.inject.Inject;

public class GetSettingsByIdUserInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final UserSettingsRepository externalUserSettingsRepository;
  private Callback<UserSettings> callback;
  private ErrorCallback errorCallback;

  @Inject public GetSettingsByIdUserInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      UserSettingsRepository externalUserSettingsRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.externalUserSettingsRepository = externalUserSettingsRepository;
  }

  public void loadSettings(Callback<UserSettings> callback, ErrorCallback errorCallback) {
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    loadRemoteSettings();
  }

  private void loadRemoteSettings() {
    try {
      notifyResult(externalUserSettingsRepository.getUserSettings());
    } catch (ServerCommunicationException error) {
      notifyError(error);
    }
  }

  private void notifyResult(final UserSettings user) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(user);
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
