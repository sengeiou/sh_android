package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.BootstrapingEntity;
import com.shootr.mobile.data.entity.FeatureFlagEntity;
import com.shootr.mobile.domain.model.Bootstrapping;
import com.shootr.mobile.domain.model.FeatureFlagType;
import com.shootr.mobile.domain.model.Socket;
import javax.inject.Inject;

public class BootstrappingEntityMapper {

  @Inject public BootstrappingEntityMapper() {
  }

  public Bootstrapping transform(BootstrapingEntity bootstrapingEntity) {
    Bootstrapping bootstrapping = new Bootstrapping();

    transformFeatureFlags(bootstrapingEntity, bootstrapping);

    Socket socket = new Socket();
    socket.setAddress(bootstrapingEntity.getSocket().getAddress());

    bootstrapping.setSocket(socket);
    bootstrapping.setLogsUrl(bootstrapingEntity.getLogsUrl());

    return bootstrapping;
  }

  private void transformFeatureFlags(BootstrapingEntity bootstrapingEntity,
      Bootstrapping bootstrapping) {
    for (FeatureFlagEntity featureFlagEntity : bootstrapingEntity.getFeatureFlags()) {
      if (featureFlagEntity.getType().equals(FeatureFlagType.SOCKET_CONNECTION)) {
        bootstrapping.setSocketConnection(true);
      } else if (featureFlagEntity.getType().equals(FeatureFlagType.TIMELINE)) {
        bootstrapping.setTimelineConnection(true);
      }
    }
  }
}
