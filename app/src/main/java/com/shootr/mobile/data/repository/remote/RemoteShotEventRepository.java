package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.repository.datasource.shot.ShotEventDataSource;
import com.shootr.mobile.domain.model.shot.ShotEvent;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotEventRepository;
import javax.inject.Inject;

public class RemoteShotEventRepository implements ShotEventRepository {

  private static final String METHOD_NOT_VALID_FOR_REPOSITORY =
      "Method not implemented in remote repository";

  private final ShotEventDataSource shotEventDataSource;

  @Inject public RemoteShotEventRepository(@Remote ShotEventDataSource shotEventDataSource) {
    this.shotEventDataSource = shotEventDataSource;
  }

  @Override public void clickLink(ShotEvent shotEvent) {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_REPOSITORY);
  }

  @Override public void viewHighlightedShot(ShotEvent shotEvent) {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_REPOSITORY);
  }

  @Override public void shotDetailViewed(ShotEvent shotEvent) {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_REPOSITORY);
  }

  @Override public void sendShotEvents() {
    shotEventDataSource.sendShotEvents();
  }
}
