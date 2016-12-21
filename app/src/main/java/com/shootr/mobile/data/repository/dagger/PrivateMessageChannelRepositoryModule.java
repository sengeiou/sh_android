package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.privateMessage.DatabasePrivateMessageChannelDataSource;
import com.shootr.mobile.data.repository.datasource.privateMessage.PrivateMessageChannelDataSource;
import com.shootr.mobile.data.repository.datasource.privateMessage.ServicePrivateMessageChannelDataSource;
import com.shootr.mobile.data.repository.local.LocalPrivateMessageChannelRepository;
import com.shootr.mobile.data.repository.remote.RemotePrivateMessageChannelRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.privateMessage.PrivateMessageChannelRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    injects = {
        RemotePrivateMessageChannelRepository.class, ServicePrivateMessageChannelDataSource.class,
        LocalPrivateMessageChannelRepository.class, DatabasePrivateMessageChannelDataSource.class,
    },
    complete = false,
    library = true) public class PrivateMessageChannelRepositoryModule {

  @Provides @Singleton @Remote
  PrivateMessageChannelRepository provideRemotePrivateMessagesChannelRepository(
      RemotePrivateMessageChannelRepository remotePrivateMessageChannelRepository) {
    return remotePrivateMessageChannelRepository;
  }

  @Provides @Singleton @Remote
  PrivateMessageChannelDataSource provideRemotePrivateMessagesChannelDataSource(
      ServicePrivateMessageChannelDataSource remotePrivateMessageChannelDataSource) {
    return remotePrivateMessageChannelDataSource;
  }

  @Provides @Singleton @Local PrivateMessageChannelRepository provideLocalPrivateMessagesChannelRepository(
      LocalPrivateMessageChannelRepository localPrivateMessageChannelRepository) {
    return localPrivateMessageChannelRepository;
  }

  @Provides @Singleton @Local PrivateMessageChannelDataSource provideLocalPrivateMessagesChannelDataSource(
      DatabasePrivateMessageChannelDataSource databasePrivateMessageChannelDataSource) {
    return databasePrivateMessageChannelDataSource;
  }

}
