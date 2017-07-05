package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.data.entity.Synchronized;
import com.shootr.mobile.data.mapper.FollowsEntityMapper;
import com.shootr.mobile.data.repository.datasource.user.FollowDataSource;
import com.shootr.mobile.data.repository.remote.cache.UserCache;
import com.shootr.mobile.data.repository.sync.SyncTrigger;
import com.shootr.mobile.data.repository.sync.SyncableRepository;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.repository.SessionRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SyncFollowRepositoryTest {

    private static final String ID_USER = "idUser";
    @Mock SessionRepository sessionRepository;
    @Mock FollowDataSource localFollowDataSource;
    @Mock FollowDataSource remoteFollowDataSource;
    @Mock FollowsEntityMapper followingEntityMapper;
    @Mock SyncTrigger syncTrigger;
    @Mock UserCache userCache;

    private SyncFollowRepository syncFollowRepository;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        syncFollowRepository = new SyncFollowRepository(sessionRepository,
          localFollowDataSource,
          remoteFollowDataSource, followingEntityMapper, syncTrigger,
          userCache);
    }

    @Test public void shouldNotInvalidatePeopleWhenFollowAndThrowServerComunicationException() throws Exception {
        doThrow(ServerCommunicationException.class).when(remoteFollowDataSource).putFollow(any(FollowEntity.class));

        syncFollowRepository.follow(ID_USER);

        verify(userCache, never()).invalidatePeople();
    }

    @Test public void shouldNotifyNeedsSyncWhenFollowAndThrowServerComunicationException() throws Exception {
        doThrow(ServerCommunicationException.class).when(remoteFollowDataSource).putFollow(any(FollowEntity.class));

        syncFollowRepository.follow(ID_USER);

        verify(syncTrigger).notifyNeedsSync(any(SyncableRepository.class));
    }

    @Test public void shouldNotInvalidatePeopleWhenUnfollowAndThrowServerComunicationException() throws Exception {
        doThrow(ServerCommunicationException.class).when(remoteFollowDataSource).removeFollow(anyString());

        syncFollowRepository.unfollow(ID_USER);

        verify(userCache, never()).invalidatePeople();
    }

    @Test public void shouldNotifyNeedsSyncWhenUnfollowAndThrowServerComunicationException() throws Exception {
        doThrow(ServerCommunicationException.class).when(remoteFollowDataSource).removeFollow(anyString());

        syncFollowRepository.unfollow(ID_USER);

        verify(syncTrigger).notifyNeedsSync(any(SyncableRepository.class));
    }

    @Test public void shouldInvalidatePeopleWhenDispatchPeopleAndAlmostOneEntityNotSynchronized() throws Exception {
        when(localFollowDataSource.getEntitiesNotSynchronized()).thenReturn(followEntitiesSyncDeleted());

        syncFollowRepository.dispatchSync();

        verify(userCache).invalidatePeople();
    }

    @Test public void shouldNotInvalidatePeopleWhenDispatchPeopleAndZeroEntityNotSynchronized() throws Exception {
        when(localFollowDataSource.getEntitiesNotSynchronized()).thenReturn(new ArrayList<FollowEntity>());

        syncFollowRepository.dispatchSync();

        verify(userCache, never()).invalidatePeople();
    }

    @Test public void shouldRemoveRemoteFollowWhenDispatchSyncAndAlmostOneSyncDeletedEntitiy() throws Exception {
        when(localFollowDataSource.getEntitiesNotSynchronized()).thenReturn(followEntitiesSyncDeleted());

        syncFollowRepository.dispatchSync();

        verify(remoteFollowDataSource).removeFollow(anyString());
    }

    @Test public void shouldPutRemoteFollowWhenDispatchSyncAndAlmostOneEntityIsNotSyncDeleted() throws Exception {
        when(localFollowDataSource.getEntitiesNotSynchronized()).thenReturn(followEntitiesSyncNew());

        syncFollowRepository.dispatchSync();

        verify(remoteFollowDataSource).putFollow(any(FollowEntity.class));
    }

    private FollowEntity followEntitySyncDeleted() {
        FollowEntity followEntity = new FollowEntity();
        followEntity.setIdUser(ID_USER);
        followEntity.setSynchronizedStatus(Synchronized.SYNC_DELETED);

        return followEntity;
    }

    private FollowEntity followEntitySyncNew() {
        FollowEntity followEntity = new FollowEntity();
        followEntity.setIdUser(ID_USER);
        followEntity.setSynchronizedStatus(Synchronized.SYNC_NEW);

        return followEntity;
    }

    private List<FollowEntity> followEntitiesSyncDeleted() {
        ArrayList<FollowEntity> followEntities = new ArrayList<>();
        followEntities.add(followEntitySyncDeleted());
        return followEntities;
    }

    private List<FollowEntity> followEntitiesSyncNew() {
        ArrayList<FollowEntity> followEntities = new ArrayList<>();
        followEntities.add(followEntitySyncNew());
        return followEntities;
    }
}
