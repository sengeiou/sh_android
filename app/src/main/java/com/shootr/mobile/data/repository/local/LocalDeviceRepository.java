package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.entity.DeviceEntity;
import com.shootr.mobile.data.mapper.DeviceEntityMapper;
import com.shootr.mobile.db.manager.DeviceManager;
import com.shootr.mobile.domain.Device;
import com.shootr.mobile.domain.repository.DeviceRepository;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class LocalDeviceRepository implements DeviceRepository {

    private final DeviceManager deviceManager;
    private final DeviceEntityMapper mapper;

    @Inject public LocalDeviceRepository(DeviceManager deviceManager, DeviceEntityMapper mapper) {
        this.deviceManager = deviceManager;
        this.mapper = mapper;
    }

    @Override public Device getCurrentDevice() {
        DeviceEntity existingDevice = deviceManager.getDevice();
        return mapper.transform(existingDevice);
    }

    @Override public Device putDevice(Device device) {
        checkNotNull(device.getIdDevice());
        deviceManager.saveDevice(mapper.transform(device));
        return device;
    }
}
