package com.shootr.android.data.repository.remote;

import com.shootr.android.data.mapper.SuggestedPeopleEntityMapper;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.data.repository.datasource.user.CachedSuggestedPeopleDataSource;
import com.shootr.android.data.repository.datasource.user.FollowDataSource;
import com.shootr.android.data.repository.datasource.user.SuggestedPeopleDataSource;
import com.shootr.android.data.repository.datasource.user.UserDataSource;
import com.shootr.android.data.repository.remote.cache.UserCache;
import com.shootr.android.data.repository.sync.SyncTrigger;
import com.shootr.android.data.repository.sync.SyncableUserEntityFactory;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.SessionRepository;
import com.squareup.otto.Bus;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SyncUserRepositoryCacheTest {

    @Mock UserDataSource localUserDataSource;
    @Mock UserDataSource remoteUserDataSource;
    @Mock SessionRepository sessionRepository;
    @Mock SuggestedPeopleDataSource remoteSuggestedPeopleDataSource;
    @Mock CachedSuggestedPeopleDataSource cachedSugestedPeopleDataSource;
    @Mock FollowDataSource localfollowDataSource;
    @Mock UserEntityMapper userEntityMapper;
    @Mock SuggestedPeopleEntityMapper suggestedPeopleEntityMapper;
    @Mock SyncableUserEntityFactory syncableUserEntityFactory;
    @Mock SyncTrigger syncTrigger;
    @Mock Bus bus;

    @Mock UserCache userCache;

    private SyncUserRepository syncUserRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        syncUserRepository = new SyncUserRepository(localUserDataSource,
          remoteUserDataSource,
          sessionRepository,
          remoteSuggestedPeopleDataSource,
          cachedSugestedPeopleDataSource,
          localfollowDataSource,
          userEntityMapper,
          suggestedPeopleEntityMapper,
          syncableUserEntityFactory,
          syncTrigger,
          bus,
          userCache);
    }

    @Test
    public void shouldNotCallRemoteRepoWhenCacheReturnsData() throws Exception {
        when(userCache.getPeople()).thenReturn(cachedUserList());

        syncUserRepository.getPeople();

        verify(remoteUserDataSource, never()).getFollowing(anyString());
    }

    @Test
    public void shouldCallRemoteRepoWhenCacheReturnsNull() throws Exception {
        when(userCache.getPeople()).thenReturn(null);

        syncUserRepository.getPeople();

        verify(remoteUserDataSource).getFollowing(anyString());
    }

    @Test
    public void shouldReturnCachedUsersWhenCacheReturnsData() throws Exception {
        when(userCache.getPeople()).thenReturn(cachedUserList());

        List<User> people = syncUserRepository.getPeople();

        assertThat(people).isEqualTo(cachedUserList());
    }

    @Test
    public void shouldPutPeopleIntoCacheWhenCacheReturnsNull() throws Exception {
        when(userCache.getPeople()).thenReturn(null);

        syncUserRepository.getPeople();

        verify(userCache).putPeople(anyListOf(User.class));
    }

    @Test
    public void shouldNotPutPeopleIntoCacheWhenCacheReturnsData() throws Exception {
        when(userCache.getPeople()).thenReturn(cachedUserList());

        syncUserRepository.getPeople();

        verify(userCache, never()).putPeople(anyListOf(User.class));
    }

    private List<User> cachedUserList() {
        return Collections.singletonList(user());
    }

    private User user() {
        User user = new User();
        user.setIdUser("id");
        return user;
    }
}