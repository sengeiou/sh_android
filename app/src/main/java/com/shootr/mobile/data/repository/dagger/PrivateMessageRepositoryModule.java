package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.privateMessage.DatabasePrivateMessageDataSource;
import com.shootr.mobile.data.repository.datasource.privateMessage.PrivateMessageDataSource;
import com.shootr.mobile.data.repository.datasource.privateMessage.ServicePrivateMessageDataSource;
import com.shootr.mobile.data.repository.local.LocalPrivateMessageRepository;
import com.shootr.mobile.data.repository.remote.RemotePrivateMessageRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.privateMessage.PrivateMessageRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    injects = {
        RemotePrivateMessageRepository.class, ServicePrivateMessageDataSource.class,
        LocalPrivateMessageRepository.class, DatabasePrivateMessageDataSource.class,
    },
    complete = false,
    library = true) public class PrivateMessageRepositoryModule {

  @Provides @Singleton @Remote PrivateMessageRepository provideRemotePrivateMessagesRepository(
      RemotePrivateMessageRepository remotePrivateMessageRepository) {
    return remotePrivateMessageRepository;
  }

  @Provides @Singleton @Remote PrivateMessageDataSource provideRemotePrivateMessagesDataSource(
      ServicePrivateMessageDataSource servicePrivateMessageDataSource) {
    return servicePrivateMessageDataSource;
  }

  @Provides @Singleton @Local PrivateMessageRepository provideLocalPrivateMessagesRepository(
      LocalPrivateMessageRepository remotePrivateMessageRepository) {
    return remotePrivateMessageRepository;
  }

  @Provides @Singleton @Local PrivateMessageDataSource provideLocalPrivateMessagesDataSource(
      DatabasePrivateMessageDataSource servicePrivateMessageDataSource) {
    return servicePrivateMessageDataSource;
  }
}
