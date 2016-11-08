package com.shootr.mobile.data.repository.datasource.shot;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.ShotEventApiService;
import com.shootr.mobile.data.entity.ShotEventEntity;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceShotEventDatasource implements ShotEventDataSource {

  public static final String METHOD_NOT_VALID_FOR_SERVICE = "Method not valid for service";

  private final ShotEventApiService shotEventApiService;
  private final DatabaseShotEventDataSource databaseShotEventDataSource;

  @Inject public ServiceShotEventDatasource(ShotEventApiService shotEventApiService,
      DatabaseShotEventDataSource databaseShotEventDataSource) {
    this.shotEventApiService = shotEventApiService;
    this.databaseShotEventDataSource = databaseShotEventDataSource;
  }

  @Override public void clickLink(ShotEventEntity shotEventEntity) {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
  }

  @Override public void sendShotEvents() {
    try {
      List<ShotEventEntity> shotEventEntities = databaseShotEventDataSource.getEvents();
      if (shotEventEntities != null && shotEventEntities.size() > 0) {
        shotEventApiService.sendShotEvents(shotEventEntities);
        databaseShotEventDataSource.deleteShotEvents();
      }
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    } catch (Exception error) {
      throw new ServerCommunicationException(error);
    }
  }

  @Override public void deleteShotEvents() {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
  }

  @Override public void viewHighlightedShot(ShotEventEntity shotEventEntity) {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
  }

  @Override public void shotDetailViewed(ShotEventEntity shotEventEntity) {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
  }
}