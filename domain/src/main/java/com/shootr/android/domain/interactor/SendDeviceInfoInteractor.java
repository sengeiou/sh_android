package com.shootr.android.domain.interactor;

import com.shootr.android.domain.Device;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.repository.DeviceRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.utils.DeviceFactory;
import javax.inject.Inject;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class SendDeviceInfoInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final DeviceRepository localDeviceRepository;
    private final DeviceRepository remoteDeviceRepository;
    private final DeviceFactory deviceFactory;

    @Inject
    public SendDeviceInfoInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Local DeviceRepository localDeviceRepository,
      @Remote DeviceRepository remoteDeviceRepository,
      DeviceFactory deviceFactory) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localDeviceRepository = localDeviceRepository;
        this.remoteDeviceRepository = remoteDeviceRepository;
        this.deviceFactory = deviceFactory;
    }

    public void sendDeviceInfo() {
        interactorHandler.execute(this);
    }

    @Override
    public void execute() throws Exception {
        Device device = localDeviceRepository.getCurrentDevice();
        if (device == null) {
            device = deviceFactory.createDevice();
        } else {
            device = deviceFactory.updateDevice(device);
        }

        Device remoteDevice = remoteDeviceRepository.putDevice(device);
        checkNotNull(remoteDevice.getIdDevice());
        localDeviceRepository.putDevice(remoteDevice);
    }
}
