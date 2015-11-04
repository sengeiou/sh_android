package com.shootr.mobile.domain.interactor;

import javax.inject.Inject;

public class SendDeviceInfoInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final com.shootr.mobile.domain.repository.DeviceRepository localDeviceRepository;
    private final com.shootr.mobile.domain.repository.DeviceRepository remoteDeviceRepository;
    private final com.shootr.mobile.domain.utils.DeviceFactory deviceFactory;

    @Inject
    public SendDeviceInfoInteractor(InteractorHandler interactorHandler,
      @com.shootr.mobile.domain.repository.Local
      com.shootr.mobile.domain.repository.DeviceRepository localDeviceRepository,
      @com.shootr.mobile.domain.repository.Remote
      com.shootr.mobile.domain.repository.DeviceRepository remoteDeviceRepository,
      com.shootr.mobile.domain.utils.DeviceFactory deviceFactory) {
        this.interactorHandler = interactorHandler;
        this.localDeviceRepository = localDeviceRepository;
        this.remoteDeviceRepository = remoteDeviceRepository;
        this.deviceFactory = deviceFactory;
    }

    public void sendDeviceInfo() {
        interactorHandler.execute(this);
    }

    @Override
    public void execute() throws Exception {
        try {
            com.shootr.mobile.domain.Device device = obtainCurrentDevice();
            com.shootr.mobile.domain.Device remoteDevice = remoteDeviceRepository.putDevice(device);
            localDeviceRepository.putDevice(remoteDevice);
        } catch (com.shootr.mobile.domain.exception.ShootrException e) {
            /* fail silently */
        }
    }

    protected com.shootr.mobile.domain.Device obtainCurrentDevice() {
        com.shootr.mobile.domain.Device device = localDeviceRepository.getCurrentDevice();
        if (device == null) {
            device = deviceFactory.createDevice();
        } else {
            device = deviceFactory.updateDevice(device);
        }
        return device;
    }
}
