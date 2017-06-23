package com.shootr.mobile.domain.interactor.device;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.Device;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.device.DeviceRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.utils.DeviceFactory;
import javax.inject.Inject;

public class SendDeviceInfoInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final DeviceRepository localDeviceRepository;
    private final DeviceRepository remoteDeviceRepository;
    private final SessionRepository sessionRepository;
    private final DeviceFactory deviceFactory;

    private String jwsResult;

    @Inject public SendDeviceInfoInteractor(InteractorHandler interactorHandler,
        @Local DeviceRepository localDeviceRepository, @Remote DeviceRepository remoteDeviceRepository,
        SessionRepository sessionRepository, DeviceFactory deviceFactory) {
        this.interactorHandler = interactorHandler;
        this.localDeviceRepository = localDeviceRepository;
        this.remoteDeviceRepository = remoteDeviceRepository;
        this.sessionRepository = sessionRepository;
        this.deviceFactory = deviceFactory;
    }

    public void sendDeviceInfo(String jwsResult) {
        this.jwsResult = jwsResult;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            Device device = obtainCurrentDevice();
            device.setAttestation(jwsResult);
            Device remoteDevice = remoteDeviceRepository.putDevice(device);
            localDeviceRepository.putDevice(remoteDevice);
            sessionRepository.setDeviceId(remoteDevice.getIdDevice());
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
