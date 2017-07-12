package com.shootr.mobile.data.repository.datasource.shot;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.ShootrEventApiService;
import com.shootr.mobile.data.entity.ShootrEventEntity;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceShootrEventDatasource implements ShootrEventDataSource {

  public static final String METHOD_NOT_VALID_FOR_SERVICE = "Method not valid for service";

  private final ShootrEventApiService shootrEventApiService;
  private final DatabaseShootrEventDataSource databaseShotEventDataSource;

  @Inject public ServiceShootrEventDatasource(ShootrEventApiService shootrEventApiService,
      DatabaseShootrEventDataSource databaseShotEventDataSource) {
    this.shootrEventApiService = shootrEventApiService;
    this.databaseShotEventDataSource = databaseShotEventDataSource;
  }

  @Override public void clickLink(ShootrEventEntity shootrEventEntity) {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
  }

  @Override public void sendShotEvents() {
    try {
      List<ShootrEventEntity> shotEventEntities = databaseShotEventDataSource.getEvents();
      if (shotEventEntities != null && shotEventEntities.size() > 0) {
        shootrEventApiService.sendShootrEvents(shotEventEntities);
        databaseShotEventDataSource.deleteShotEvents();
      }
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    } catch (Exception error) {
      throw new ServerCommunicationException(error);
    }
  }

  @Override public void viewUserProfileEvent(ShootrEventEntity shootrEventEntity) {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
  }

  @Override public void deleteShotEvents() {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
  }

  @Override public void viewHighlightedShot(ShootrEventEntity shootrEventEntity) {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
  }

  @Override public void shotDetailViewed(ShootrEventEntity shootrEventEntity) {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
  }
}