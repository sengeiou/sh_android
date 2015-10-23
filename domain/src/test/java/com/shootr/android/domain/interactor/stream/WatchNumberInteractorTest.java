package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
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

    private com.shootr.android.domain.interactor.stream.WatchNumberInteractor interactor;

    @Mock UserRepository remoteUserRepository;
    @Mock UserRepository localUserRepository;
    @Mock SessionRepository sessionRepository;
    @Spy SpyCallback spyCallback = new SpyCallback();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        TestInteractorHandler testInteractorHandler = new TestInteractorHandler();
        interactor = new com.shootr.android.domain.interactor.stream.WatchNumberInteractor(testInteractorHandler,
          postExecutionThread,
          sessionRepository,
          remoteUserRepository,
          localUserRepository);
    }

    @Test
    public void testPeopleAndMeIncludesMe() throws Exception {
        when(remoteUserRepository.getPeople()).thenReturn(onePersonList());
        when(sessionRepository.getCurrentUser()).thenReturn(me());

        List<User> peopleAndMe = interactor.getPeopleAndMe();

        assertThat(peopleAndMe).contains(me()).hasSize(2);
        assertThat(peopleAndMe.get(1).isMe()).isTrue();
    }

    @Test
    public void shouldIncludeMeInTheCount() throws Exception {
        when(remoteUserRepository.getPeople()).thenReturn(Arrays.asList(newUserWatching(ID_USER_1)));
        when(sessionRepository.getCurrentUser()).thenReturn(me());

        interactor.loadWatchNumber(STREAM_ID, spyCallback);

        assertThat(spyCallback.count).isEqualTo(2);
    }

    @Test
    public void shouldFilterOutPeopleNotWatchingTheStream() throws Exception {
        List<User> oneUserWatchingAndOneNotWatching =
          Arrays.asList(newUserWatching(ID_USER_1), newUserNotWatching(ID_USER_2));

        List<User> filteredUsers = interactor.filterUsersWatchingStream(oneUserWatchingAndOneNotWatching, STREAM_ID);

        assertThat(filteredUsers).doesNotContain(newUserNotWatching(ID_USER_2));
        assertThat(filteredUsers).contains(newUserWatching(ID_USER_1));
    }

    @Test
    public void shouldFallbackToLocalUserRepositoryWhenRemoteRepositoryFails() throws Exception {
        when(sessionRepository.getCurrentUser()).thenReturn(me());
        when(remoteUserRepository.getPeople()).thenThrow(new ServerCommunicationException(null));

        interactor.loadWatchNumber(STREAM_ID, spyCallback);

        verify(localUserRepository).getPeople();
    }

    private User me() {
        User user = newUserWatching(ID_USER_ME);
        user.setMe(true);
        user.setIdWatchingStream(STREAM_ID);
        return user;
    }

    private List<User> onePersonList() {
        List<User> users = new ArrayList<>();
        users.add(newUserWatching(ID_USER_1));
        return users;
    }

    private User newUserWatching(Long id) {
        User user = newUserNotWatching(id);
        user.setIdWatchingStream(STREAM_ID);
        return user;
    }

    private User newUserNotWatching(Long id) {
        User user = new User();
        user.setIdUser(String.valueOf(id));
        return user;
    }

    private class SpyCallback implements com.shootr.android.domain.interactor.stream.WatchNumberInteractor.Callback {

        public Integer count;

        @Override public void onLoaded(Integer count) {
            this.count = count;
        }
    }
}