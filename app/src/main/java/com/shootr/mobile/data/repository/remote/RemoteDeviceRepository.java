package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.DeviceApiService;
import com.shootr.mobile.data.entity.DeviceEntity;
import com.shootr.mobile.data.mapper.DeviceEntityMapper;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.model.Device;
import com.shootr.mobile.domain.repository.DeviceRepository;
import java.io.IOException;
import javax.inject.Inject;

public class RemoteDeviceRepository implements DeviceRepository {

    private final DeviceEntityMapper mapper;
    private final DeviceApiService deviceApiService;

    @Inject

    public RemoteDeviceRepository(DeviceEntityMapper mapper, DeviceApiService deviceApiService) {
        this.mapper = mapper;
        this.deviceApiService = deviceApiService;
    }

    @Override public Device getCurrentDevice() {
        throw new IllegalStateException("Retrieving device from remote is not implemented");
    }

    @Override public Device putDevice(Device device) {
        try {
            DeviceEntity createdDevice = deviceApiService.createUpdateDevice(mapper.transform(device));
            return mapper.transform(createdDevice);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }
}
