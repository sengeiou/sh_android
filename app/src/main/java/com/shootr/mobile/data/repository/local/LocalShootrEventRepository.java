package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.api.entity.mapper.ShootrEventEntityMapper;
import com.shootr.mobile.data.repository.datasource.shot.ShootrEventDataSource;
import com.shootr.mobile.domain.model.shot.ShootrEvent;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.shot.ShootrEventRepository;
import javax.inject.Inject;

public class LocalShootrEventRepository implements ShootrEventRepository {

  private final ShootrEventDataSource shootrEventDataSource;
  private final ShootrEventEntityMapper shootrEventEntityMapper;

  @Inject public LocalShootrEventRepository(@Local ShootrEventDataSource shootrEventDataSource,
      ShootrEventEntityMapper shootrEventEntityMapper) {
    this.shootrEventDataSource = shootrEventDataSource;
    this.shootrEventEntityMapper = shootrEventEntityMapper;
  }

  @Override public void clickLink(ShootrEvent shootrEvent) {
    shootrEventDataSource.clickLink(shootrEventEntityMapper.transform(shootrEvent));
  }

  @Override public void viewHighlightedShot(ShootrEvent shootrEvent) {
    shootrEventDataSource.viewHighlightedShot(shootrEventEntityMapper.transform(shootrEvent));
  }

  @Override public void shotDetailViewed(ShootrEvent shootrEvent) {
    shootrEventDataSource.shotDetailViewed(shootrEventEntityMapper.transform(shootrEvent));
  }

  @Override public void sendShotEvents() {
    throw new IllegalStateException("Method not valid for local repository");
  }

  @Override public void viewUserProfileEvent(ShootrEvent shootrEvent) {
    shootrEventDataSource.viewUserProfileEvent(shootrEventEntityMapper.transform(shootrEvent));
  }

  @Override public void getShootrEvents() {
    throw new IllegalStateException("Method not valid for local repository");
  }
}
