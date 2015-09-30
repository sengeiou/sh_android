package com.shootr.android.data.repository.remote;

import com.shootr.android.data.api.service.DeviceApiService;
import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.data.mapper.DeviceEntityMapper;
import com.shootr.android.domain.Device;
import com.shootr.android.domain.repository.DeviceRepository;
import javax.inject.Inject;

public class RemoteDeviceRepository implements DeviceRepository {

    private final DeviceEntityMapper mapper;
    private final DeviceApiService deviceApiService;

    @Inject

    public RemoteDeviceRepository(DeviceEntityMapper mapper, DeviceApiService deviceApiService) {
        this.mapper = mapper;
        this.deviceApiService = deviceApiService;
    }

    @Override
    public Device getCurrentDevice() {
        throw new IllegalStateException("Retrieving device from remote is not implemented");
    }

    @Override
    public Device putDevice(Device device) {
        DeviceEntity createdDevice = deviceApiService.createUpdateDevice(mapper.transform(device));
        return mapper.transform(createdDevice);
    }
}
