package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WatchNumberInteractorTest {

    public static final long ID_USER_ME = 666L;
    public static final long ID_USER_1 = 1L;
    public static final long ID_USER_2 = 2L;
    public static final String STREAM_ID = "id";

    private WatchNumberInteractor interactor;

    @Mock com.shootr.mobile.domain.repository.UserRepository remoteUserRepository;
    @Mock com.shootr.mobile.domain.repository.UserRepository localUserRepository;
    @Spy SpyCallback spyCallback = new SpyCallback();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        TestInteractorHandler testInteractorHandler = new TestInteractorHandler();
        interactor = new WatchNumberInteractor(testInteractorHandler,
          postExecutionThread,
          remoteUserRepository,
          localUserRepository);
    }

    @Test
    public void shouldFilterOutPeopleNotWatchingTheStream() throws Exception {
        List<com.shootr.mobile.domain.User> oneUserWatchingAndOneNotWatching =
          Arrays.asList(newUserWatching(ID_USER_1), newUserNotWatching(ID_USER_2));

        List<com.shootr.mobile.domain.User> filteredUsers = interactor.filterUsersWatchingStream(oneUserWatchingAndOneNotWatching, STREAM_ID);

        assertThat(filteredUsers).doesNotContain(newUserNotWatching(ID_USER_2));
        assertThat(filteredUsers).contains(newUserWatching(ID_USER_1));
    }

    @Test
    public void shouldFallbackToLocalUserRepositoryWhenRemoteRepositoryFails() throws Exception {
        when(remoteUserRepository.getPeople()).thenThrow(new com.shootr.mobile.domain.exception.ServerCommunicationException(null));

        interactor.loadWatchNumber(STREAM_ID, spyCallback);

        verify(localUserRepository).getPeople();
    }

    private com.shootr.mobile.domain.User me() {
        com.shootr.mobile.domain.User user = newUserWatching(ID_USER_ME);
        user.setMe(true);
        user.setIdWatchingStream(STREAM_ID);
        return user;
    }

    private com.shootr.mobile.domain.User newUserWatching(Long id) {
        com.shootr.mobile.domain.User user = newUserNotWatching(id);
        user.setIdWatchingStream(STREAM_ID);
        return user;
    }

    private com.shootr.mobile.domain.User newUserNotWatching(Long id) {
        com.shootr.mobile.domain.User user = new com.shootr.mobile.domain.User();
        user.setIdUser(String.valueOf(id));
        return user;
    }

    private class SpyCallback implements WatchNumberInteractor.Callback {

        public Integer count;

        @Override public void onLoaded(Integer count) {
            this.count = count;
        }
    }
}