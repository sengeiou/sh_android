package com.shootr.mobile.domain.interactor.device;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.Device;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.device.DeviceRepository;
import com.shootr.mobile.domain.utils.DeviceFactory;
import javax.inject.Inject;

public class ShouldUpdateDeviceInfoInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final DeviceRepository localDeviceRepository;
  private final SessionRepository sessionRepository;
  private final DeviceFactory deviceFactory;

  private Callback<Boolean> callback;

  @Inject public ShouldUpdateDeviceInfoInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local DeviceRepository localDeviceRepository,
      SessionRepository sessionRepository, DeviceFactory deviceFactory) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localDeviceRepository = localDeviceRepository;
    this.sessionRepository = sessionRepository;
    this.deviceFactory = deviceFactory;
  }

  public void getDeviceInfo(Callback<Boolean> callback) {
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      Device device = getDeviceFromLocal();
      notifyLoaded(device == null || deviceFactory.needsUpdate(device));
    } catch (ShootrException e) {
            /* fail silently */
    }
  }

  private Device getDeviceFromLocal() {
    Device device = localDeviceRepository.getCurrentDevice();
    if (device == null) {
      device = sessionRepository.getDevice();
      localDeviceRepository.putDevice(device);
    }

    return device;
  }

  private void notifyLoaded(final Boolean result) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(result);
      }
    });
  }
}
