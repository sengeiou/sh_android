package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.repository.datasource.shot.ShootrEventDataSource;
import com.shootr.mobile.domain.model.shot.ShootrEvent;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.shot.ShootrEventRepository;
import javax.inject.Inject;

public class RemoteShootrEventRepository implements ShootrEventRepository {

  private static final String METHOD_NOT_VALID_FOR_REPOSITORY =
      "Method not implemented in remote repository";

  private final ShootrEventDataSource shootrEventDataSource;

  @Inject public RemoteShootrEventRepository(@Remote ShootrEventDataSource shootrEventDataSource) {
    this.shootrEventDataSource = shootrEventDataSource;
  }

  @Override public void clickLink(ShootrEvent shootrEvent) {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_REPOSITORY);
  }

  @Override public void viewHighlightedShot(ShootrEvent shootrEvent) {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_REPOSITORY);
  }

  @Override public void shotDetailViewed(ShootrEvent shootrEvent) {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_REPOSITORY);
  }

  @Override public void timelineViewed(ShootrEvent shootrEvent) {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_REPOSITORY);
  }

  @Override public void sendShotEvents() {
    shootrEventDataSource.sendShotEvents();
  }

  @Override public void viewUserProfileEvent(ShootrEvent shootrEvent) {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_REPOSITORY);
  }

  @Override public void getShootrEvents() {
    shootrEventDataSource.getShootrEvents();
  }
}
