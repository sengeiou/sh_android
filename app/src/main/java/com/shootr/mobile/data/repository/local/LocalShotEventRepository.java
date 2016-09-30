package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.api.entity.mapper.ShotEventEntityMapper;
import com.shootr.mobile.data.repository.datasource.shot.ShotEventDataSource;
import com.shootr.mobile.domain.model.shot.ShotEvent;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.shot.ShotEventRepository;
import javax.inject.Inject;

public class LocalShotEventRepository implements ShotEventRepository {

  private final ShotEventDataSource shotEventDataSource;
  private final ShotEventEntityMapper shotEventEntityMapper;

  @Inject public LocalShotEventRepository(@Local ShotEventDataSource shotEventDataSource,
      ShotEventEntityMapper shotEventEntityMapper) {
    this.shotEventDataSource = shotEventDataSource;
    this.shotEventEntityMapper = shotEventEntityMapper;
  }

  @Override public void clickLink(ShotEvent shotEvent) {
    shotEventDataSource.clickLink(shotEventEntityMapper.transform(shotEvent));
  }

  @Override public void viewHighlightedShot(ShotEvent shotEvent) {
    shotEventDataSource.viewHighlightedShot(shotEventEntityMapper.transform(shotEvent));
  }

  @Override public void shotDetailViewed(ShotEvent shotEvent) {
    shotEventDataSource.shotDetailViewed(shotEventEntityMapper.transform(shotEvent));
  }

  @Override public void sendShotEvents() {
    throw new IllegalStateException("Method not valid for local repository");
  }
}
