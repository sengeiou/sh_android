package com.shootr.mobile.data.repository.datasource.favorite;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.OnBoardingApiService;
import com.shootr.mobile.data.entity.OnBoardingEntity;
import com.shootr.mobile.data.entity.OnBoardingFavoritesEntity;
import com.shootr.mobile.data.repository.datasource.stream.StreamDataSource;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.model.FollowableType;
import com.shootr.mobile.domain.repository.Local;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ServiceOnBoardingDataSource implements ExternalOnBoardingDatasource {

  private final OnBoardingApiService onBoardingApiService;
  private final StreamDataSource localStreamDataSource;

  @Inject public ServiceOnBoardingDataSource(OnBoardingApiService onBoardingApiService,
      @Local StreamDataSource localStreamDataSource) {
    this.onBoardingApiService = onBoardingApiService;
    this.localStreamDataSource = localStreamDataSource;
  }

  @Override public List<OnBoardingEntity> getOnBoarding(String type, String locale) {
    try {
      List<OnBoardingEntity> onBoardingStreamEntities =
          onBoardingApiService.getFavoritesOnboarding(type, locale);
      if (onBoardingStreamEntities != null) {
        if (type.equals(FollowableType.STREAM)) {
          storeOnBoardingStreams(onBoardingStreamEntities);
        }
        return onBoardingStreamEntities;
      } else {
        return new ArrayList<>();
      }
    } catch (ApiException | IOException error) {
      throw new ServerCommunicationException(error);
    }
  }

  private void storeOnBoardingStreams(List<OnBoardingEntity> onBoardingStreamEntities) {
    for (OnBoardingEntity onBoardingStreamEntity : onBoardingStreamEntities) {
      if (onBoardingStreamEntity.getStreamEntity() != null) {
        localStreamDataSource.putStream(onBoardingStreamEntity.getStreamEntity());
      }
    }
  }

  @Override public void addFavorites(List<String> idOnBoardings, String type) {
    try {
      OnBoardingFavoritesEntity onBoardingFavoritesEntity = new OnBoardingFavoritesEntity();
      if (type.equals(FollowableType.STREAM)) {
        onBoardingFavoritesEntity.setIdStreams(idOnBoardings);
      } else if (type.equals(FollowableType.USER)) {
        onBoardingFavoritesEntity.setIdUsers(idOnBoardings);
      }
      onBoardingApiService.addOnBoardingFavorites(onBoardingFavoritesEntity);
    } catch (ApiException | IOException error) {
      throw new ServerCommunicationException(error);
    }
  }

}
