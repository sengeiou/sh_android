package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.Device;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.repository.DeviceRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.utils.DeviceFactory;
import javax.inject.Inject;

public class SendDeviceInfoInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final DeviceRepository localDeviceRepository;
    private final DeviceRepository remoteDeviceRepository;
    private final DeviceFactory deviceFactory;

    @Inject
    public SendDeviceInfoInteractor(InteractorHandler interactorHandler, @Local DeviceRepository localDeviceRepository,
      @Remote DeviceRepository remoteDeviceRepository, DeviceFactory deviceFactory) {
        this.interactorHandler = interactorHandler;
        this.localDeviceRepository = localDeviceRepository;
        this.remoteDeviceRepository = remoteDeviceRepository;
        this.deviceFactory = deviceFactory;
    }

    public void sendDeviceInfo() {
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            Device device = obtainCurrentDevice();
            Device remoteDevice = remoteDeviceRepository.putDevice(device);
            localDeviceRepository.putDevice(remoteDevice);
        } catch (ShootrException e) {
            /* fail silently */
        }
    }

    protected Device obtainCurrentDevice() {
        Device device = localDeviceRepository.getCurrentDevice();
        if (device == null) {
            device = deviceFactory.createDevice();
        } else {
            device = deviceFactory.updateDevice(device);
        }
        return device;
    }
}
