package com.shootr.android.domain.interactor;

import com.shootr.android.domain.Device;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.repository.DeviceRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.utils.DeviceFactory;
import javax.inject.Inject;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class SendDeviceInfoInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final DeviceRepository localDeviceRepository;
    private final DeviceRepository remoteDeviceRepository;
    private final DeviceFactory deviceFactory;

    @Inject
    public SendDeviceInfoInteractor(InteractorHandler interactorHandler,
      @Local DeviceRepository localDeviceRepository,
      @Remote DeviceRepository remoteDeviceRepository,
      DeviceFactory deviceFactory) {
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
            Device device = obtainCurrentDevice();
            Device remoteDevice = remoteDeviceRepository.putDevice(device);
            checkNotNull(remoteDevice.getIdDevice());
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
