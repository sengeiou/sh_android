package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.repository.WatchRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventsWatchedCountInteractorTest {

    public static final long ID_EVENT_A = 1L;
    public static final long ID_EVENT_B = 2L;
    public static final long ID_EVENT_C = 3L;

    public static final long ID_USER_1 = 1L;
    public static final long ID_USER_2 = 2L;
    public static final long ID_USER_3 = 3L;
    public static final long ID_USER_4 = 4L;
    public static final long ID_USER_ME = 666L;

    TestInteractorHandler testInteractorHandler;
    TestPostExecutionThread postExecutionThread;

    @Mock SessionRepository sessionRepository;
    @Mock UserRepository userRepository;
    @Mock WatchRepository watchRepository;
    @Mock EventsWatchedCountInteractor.Callback callback;

    @Mock Interactor.InteractorErrorCallback errorCallback;
    private EventsWatchedCountInteractor interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        testInteractorHandler = new TestInteractorHandler();
        postExecutionThread = new TestPostExecutionThread();
        interactor = new EventsWatchedCountInteractor(testInteractorHandler, postExecutionThread, sessionRepository,
          watchRepository, userRepository);
    }

    @Test
    public void eventCountIsThreeInFiveWatchesWithThreeDifferentEvents() throws Exception {
        Integer countFromWatches = interactor.countDifferentEventsInWatches(fiveWatchesWithThreeEvents());
        assertThat(countFromWatches).isEqualTo(3);
    }

    @Test
    public void eventCountIsZeroWhenNoFollowing() throws Exception {
        when(userRepository.getPeople()).thenReturn(Collections.<User>emptyList());
        when(watchRepository.getWatchesFromUsers(anyListOf(Long.class))).thenReturn(Collections.<Watch>emptyList());
        interactor.obtainEventsCount(callback, errorCallback);

        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(callback).onLoaded(argumentCaptor.capture());
        Integer zeroCount = argumentCaptor.getValue();
        assertThat(zeroCount).isEqualTo(0);
    }

    @Test
    public void eventCountIsOneWhenImNotWachingAndOneFollowingIsWatchingSomethingElse() throws Exception {
        when(watchRepository.getWatchesFromUsers(anyListOf(Long.class))).thenReturn(meNotWatchingAndOtherWatching());
        interactor.obtainEventsCount(callback, errorCallback);

        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(callback).onLoaded(argumentCaptor.capture());
        Integer countOne = argumentCaptor.getValue();
        assertThat(countOne).isEqualTo(1);
    }

    //TODO don't know how to test that current user's watch is included

    //region Stubs
    private List<Watch> meNotWatchingAndOtherWatching() {
        List<Watch> watches = new ArrayList<>();
        watches.add(newWatchNotWatching(ID_EVENT_A, ID_USER_ME));
        watches.add(newWatch(ID_EVENT_B, ID_USER_1));
        return watches;
    }

    private Watch newWatchNotWatching(Long idEvent, Long idUser) {
        Watch watch = newWatch(idEvent, idUser);
        watch.setWatching(false);
        return watch;
    }

    private List<Watch> fiveWatchesWithThreeEvents() {
        List<Watch> watches = new ArrayList<>();
        watches.add(newWatch(ID_EVENT_A, ID_USER_1));
        watches.add(newWatch(ID_EVENT_B, ID_USER_2));
        watches.add(newWatch(ID_EVENT_C, ID_USER_3));
        watches.add(newWatch(ID_EVENT_B, ID_USER_4));
        watches.add(newWatch(ID_EVENT_A, ID_USER_3));
        return watches;
    }

    private Watch newWatch(Long idEvent, Long idUser) {
        Watch watch = new Watch();
        watch.setWatching(true);
        watch.setIdEvent(idEvent);

        User user = new User();
        user.setIdUser(idUser);
        watch.setUser(user);
        return watch;
    }
    //endregion
}