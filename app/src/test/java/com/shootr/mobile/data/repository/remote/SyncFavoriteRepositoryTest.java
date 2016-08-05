package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.FavoriteEntity;
import com.shootr.mobile.data.entity.Synchronized;
import com.shootr.mobile.data.mapper.FavoriteEntityMapper;
import com.shootr.mobile.data.repository.datasource.favorite.ExternalFavoriteDatasource;
import com.shootr.mobile.data.repository.datasource.favorite.InternalFavoriteDatasource;
import com.shootr.mobile.data.repository.sync.SyncTrigger;
import com.shootr.mobile.data.repository.sync.SyncableFavoriteEntityFactory;
import com.shootr.mobile.data.repository.sync.SyncableRepository;
import com.shootr.mobile.domain.model.stream.Favorite;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.StreamAlreadyInFavoritesException;
import com.shootr.mobile.domain.repository.SessionRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SyncFavoriteRepositoryTest {

    public static final String ID_STREAM = "idStream";
    private static final String ID_CURRENT_USER = "idUser";
    private static final String ID_OTHER_USER = "idOtherUser";
    @Mock ExternalFavoriteDatasource remoteFavoriteDataSource;
    @Mock InternalFavoriteDatasource localFavoriteDataSource;
    @Mock FavoriteEntityMapper favoriteEntityMapper;
    @Mock SyncableFavoriteEntityFactory syncableFavoriteEntityFactory;
    @Mock SyncTrigger syncTrigger;
    @Mock SessionRepository sessionRepository;

    private SyncFavoriteRepository syncFavoriteRepository;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        syncFavoriteRepository = new SyncFavoriteRepository(remoteFavoriteDataSource,
          localFavoriteDataSource,
          favoriteEntityMapper,
          syncableFavoriteEntityFactory,
          syncTrigger,
          sessionRepository);
    }

    @Test public void shouldSyncTriggerWhenPutFavorite() throws Exception {
        when(remoteFavoriteDataSource.putFavorite(any(FavoriteEntity.class))).thenReturn(favoriteEntity());

        syncFavoriteRepository.putFavorite(any(Favorite.class));

        verify(syncTrigger).triggerSync();
    }

    @Test public void shouldNotifyNeedsSyncWhenPutFavoriteAndThrowServerComunicatoinException() throws Exception {
        when(syncableFavoriteEntityFactory.updatedOrNewEntity(any(Favorite.class))).thenReturn(favoriteEntity());
        when(remoteFavoriteDataSource.putFavorite(any(FavoriteEntity.class))).thenReturn(favoriteEntity());
        doThrow(ServerCommunicationException.class).when(syncTrigger).triggerSync();

        syncFavoriteRepository.putFavorite(favorite());

        verify(syncTrigger).notifyNeedsSync(any(SyncableRepository.class));
    }

    @Test public void shouldPutFavoritesInLocalWhenGetFavoritesFromRemoteAndIsCurrentUser() throws Exception {
        when(remoteFavoriteDataSource.getFavorites(ID_CURRENT_USER)).thenReturn(favoriteEntities());
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_CURRENT_USER);

        syncFavoriteRepository.getFavorites(ID_CURRENT_USER);

        verify(localFavoriteDataSource, atLeastOnce()).putFavorite(any(FavoriteEntity.class));
    }

    @Test public void shouldNotPutFavoritesInLocalWhenGetFavoritesFromRemoteAndIsNotCurrentUser() throws Exception {
        when(remoteFavoriteDataSource.getFavorites(ID_OTHER_USER)).thenReturn(favoriteEntities());
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_CURRENT_USER);

        syncFavoriteRepository.getFavorites(ID_CURRENT_USER);

        verify(localFavoriteDataSource, never()).putFavorite(any(FavoriteEntity.class));
    }

    @Test public void shouldNotifyNeedSyncWhenRemoveFavoritesByStreamAndThrowServerComunicatoinException()
      throws Exception {
        doThrow(ServerCommunicationException.class).when(remoteFavoriteDataSource).removeFavoriteByIdStream(ID_STREAM);

        syncFavoriteRepository.removeFavoriteByStream(ID_STREAM);

        verify(syncTrigger).notifyNeedsSync(any(SyncableRepository.class));
    }

    @Test public void shouldRemoveFavoriteByIdStreamWhenDispatchSyncAndFavoriteEntityIsSyncDeleted() throws Exception {
        when(localFavoriteDataSource.getEntitiesNotSynchronized()).thenReturn(favoriteEntitiesSyncDeleted());

        syncFavoriteRepository.dispatchSync();

        verify(remoteFavoriteDataSource).removeFavoriteByIdStream(anyString());
    }

    @Test public void shouldPutFavoriteWhenDispatchSyncAndFavoriteEntityIsNotSyncDeleted() throws Exception {
        when(localFavoriteDataSource.getEntitiesNotSynchronized()).thenReturn(favoriteEntities());
        when(remoteFavoriteDataSource.putFavorite(any(FavoriteEntity.class))).thenReturn(favoriteEntity());

        syncFavoriteRepository.dispatchSync();

        verify(remoteFavoriteDataSource).putFavorite(any(FavoriteEntity.class));
    }

    @Test
    public void shouldNotPutFavoriteWhenDispatchSyncAndFavoriteEntityIsNotSyncDeletedAndThrowsStreamAlreadyInFavorites()
      throws Exception {
        when(remoteFavoriteDataSource.putFavorite(any(FavoriteEntity.class))).thenThrow(
          StreamAlreadyInFavoritesException.class);
        when(localFavoriteDataSource.getEntitiesNotSynchronized()).thenReturn(favoriteEntities());

        syncFavoriteRepository.dispatchSync();

        verify(localFavoriteDataSource, never()).putFavorite(any(FavoriteEntity.class));
    }

    private FavoriteEntity favoriteEntity() {
        FavoriteEntity favoriteEntity = new FavoriteEntity();
        favoriteEntity.setIdStream(ID_STREAM);
        return favoriteEntity;
    }

    private FavoriteEntity favoriteEntitySyncDeleted() {
        FavoriteEntity favoriteEntity = new FavoriteEntity();
        favoriteEntity.setIdStream(ID_STREAM);
        favoriteEntity.setSynchronizedStatus(Synchronized.SYNC_DELETED);
        return favoriteEntity;
    }

    private Favorite favorite() {
        Favorite favorite = new Favorite();
        favorite.setIdStream(ID_STREAM);
        return favorite;
    }

    private List<FavoriteEntity> favoriteEntities() {
        ArrayList<FavoriteEntity> favoritesEntities = new ArrayList<>();
        favoritesEntities.add(favoriteEntity());
        return favoritesEntities;
    }

    private List<FavoriteEntity> favoriteEntitiesSyncDeleted() {
        ArrayList<FavoriteEntity> favoritesEntities = new ArrayList<>();
        favoritesEntities.add(favoriteEntitySyncDeleted());
        return favoritesEntities;
    }
}
