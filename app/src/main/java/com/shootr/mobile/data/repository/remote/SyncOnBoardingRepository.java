package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.mapper.OnBoardingEntityMapper;
import com.shootr.mobile.data.repository.datasource.favorite.ExternalOnBoardingDatasource;
import com.shootr.mobile.data.repository.sync.SyncableRepository;
import com.shootr.mobile.domain.model.stream.OnBoarding;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.onBoarding.ExternalOnBoardingRepository;
import java.util.List;
import javax.inject.Inject;

public class SyncOnBoardingRepository implements ExternalOnBoardingRepository, SyncableRepository {

  private final ExternalOnBoardingDatasource remoteFavoriteDataSource;
  private final OnBoardingEntityMapper onBoardingEntityMapper;
  private final SessionRepository sessionRepository;

  @Inject public SyncOnBoardingRepository(ExternalOnBoardingDatasource remoteFavoriteDataSource,
      OnBoardingEntityMapper suggestedStreamEntityMapper,
      SessionRepository sessionRepository) {
    this.remoteFavoriteDataSource = remoteFavoriteDataSource;
    this.onBoardingEntityMapper = suggestedStreamEntityMapper;
    this.sessionRepository = sessionRepository;
  }

  @Override public List<OnBoarding> getOnBoardingStreams(String type, String locale) {
    return onBoardingEntityMapper.map(remoteFavoriteDataSource.getOnBoarding(type, locale),
        sessionRepository.getCurrentUserId());
  }

  @Override public List<OnBoarding> getOnBoardingUsers(String type, String locale) {
    return onBoardingEntityMapper.map(remoteFavoriteDataSource.getOnBoarding(type, locale),
        sessionRepository.getCurrentUserId());
  }

  @Override public void addSuggestedFavorites(List<String> idStreams, String type) {
    remoteFavoriteDataSource.addFavorites(idStreams, type);
  }

  @Override public void dispatchSync() {

  }
}
