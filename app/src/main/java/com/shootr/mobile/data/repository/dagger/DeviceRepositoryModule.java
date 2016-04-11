package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.local.LocalDeviceRepository;
import com.shootr.mobile.data.repository.remote.RemoteDeviceRepository;
import com.shootr.mobile.domain.repository.DeviceRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;

import dagger.Module;
import dagger.Provides;

@Module(
  injects = {
  },
  complete = false,
  library = true) public class DeviceRepositoryModule {

    @Provides
    @Local
    DeviceRepository provideLocalDeviceRepository(LocalDeviceRepository localDeviceRepository) {
        return localDeviceRepository;
    }

    @Provides
    @Remote
    DeviceRepository provideRemoteDeviceRepository(RemoteDeviceRepository remoteDeviceRepository) {
        return remoteDeviceRepository;
    }
}
