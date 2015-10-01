package com.shootr.android.data.repository.dagger;

import com.shootr.android.data.repository.local.LocalDeviceRepository;
import com.shootr.android.data.repository.remote.RemoteDeviceRepository;
import com.shootr.android.domain.repository.DeviceRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
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
