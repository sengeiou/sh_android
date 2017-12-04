package com.shootr.mobile.data.repository.datasource.shot;

import com.shootr.mobile.data.api.entity.FollowsEntity;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.ShootrEventApiService;
import com.shootr.mobile.data.entity.ShootrEventEntity;
import com.shootr.mobile.data.repository.datasource.stream.DatabaseRecentSearchDataSource;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceShootrEventDatasource implements ShootrEventDataSource {

  public static final String METHOD_NOT_VALID_FOR_SERVICE = "Method not valid for service";

  private final ShootrEventApiService shootrEventApiService;
  private final DatabaseShootrEventDataSource databaseShotEventDataSource;
  private final DatabaseRecentSearchDataSource databaseRecentSearchDataSource;

  @Inject public ServiceShootrEventDatasource(ShootrEventApiService shootrEventApiService,
      DatabaseShootrEventDataSource databaseShotEventDataSource,
      DatabaseRecentSearchDataSource databaseRecentSearchDataSource) {
    this.shootrEventApiService = shootrEventApiService;
    this.databaseShotEventDataSource = databaseShotEventDataSource;
    this.databaseRecentSearchDataSource = databaseRecentSearchDataSource;
  }

  @Override public void clickLink(ShootrEventEntity shootrEventEntity) {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
  }

  @Override public void sendShotEvents() {
    try {
      List<ShootrEventEntity> shotEventEntities = databaseShotEventDataSource.getEvents();
      if (shotEventEntities != null && shotEventEntities.size() > 0) {
        shootrEventApiService.sendShootrEvents(shotEventEntities);
        databaseShotEventDataSource.deleteShootrEvents();
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

  @Override public void deleteShootrEvents() {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
  }

  @Override public void getShootrEvents() {
    try {
      FollowsEntity recentList = shootrEventApiService.getRecentList();
      databaseRecentSearchDataSource.putRecentSearchItems(recentList);
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    } catch (Exception error) {
      throw new ServerCommunicationException(error);
    }
  }

  @Override public void timelineViewed(ShootrEventEntity shootrEvent) {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
  }

  @Override public void viewHighlightedShot(ShootrEventEntity shootrEventEntity) {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
  }

  @Override public void shotDetailViewed(ShootrEventEntity shootrEventEntity) {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
  }
}