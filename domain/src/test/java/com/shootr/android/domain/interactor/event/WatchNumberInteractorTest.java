package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.repository.WatchRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class WatchNumberInteractorTest {

    public static final long ID_USER_ME = 666L;
    public static final long ANY_ID_EVENT = 1L;
    public static final long ID_USER_1 = 1L;
    public static final long ID_USER_2 = 2L;

    private WatchNumberInteractor interactor;
    private PostExecutionThread postExecutionThread;

    private TestInteractorHandler testInteractorHandler;
    @Mock UserRepository userRepository;
    @Mock SessionRepository sessionRepository;
    @Mock WatchRepository watchRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        postExecutionThread = new TestPostExecutionThread();
        testInteractorHandler = new TestInteractorHandler();
        interactor = new WatchNumberInteractor(testInteractorHandler, postExecutionThread, sessionRepository, userRepository,
          watchRepository);
    }

    @Test
    public void testPeopleAndMeIncludesMe() throws Exception {
        when(userRepository.getPeople()).thenReturn(onePersonList());
        when(sessionRepository.getCurrentUser()).thenReturn(me());

        List<User> peopleAndMe = interactor.getPeopleAndMe();

        assertThat(peopleAndMe).contains(me());
        assertThat(peopleAndMe).hasSize(2);
        assertThat(peopleAndMe.get(1).isMe()).isTrue();
    }

    @Test
    public void testCountOnlyIncludesIsWatching() throws Exception {
        Integer oneCount = interactor.countIsVisible(watchingAndNotWatching());
        assertThat(oneCount).isEqualTo(1);
    }

    private List<Watch> watchingAndNotWatching() {
        List<Watch> watches = new ArrayList<>();
        watches.add(newWatch(ANY_ID_EVENT, ID_USER_1));
        watches.add(newWatchNotWatching(ANY_ID_EVENT, ID_USER_2));
        return watches;
    }

    private Watch newWatchNotWatching(Long idEvent, Long idUser) {
        Watch watch = newWatch(idEvent, idUser);
        watch.setVisible(false);
        return watch;
    }

    private Watch newWatch(Long idEvent, Long idUser) {
        Watch watch = new Watch();
        watch.setIdEvent(idEvent);
        watch.setUser(newUser(idUser));
        watch.setVisible(true);
        return watch;
    }

    private User me() {
        User user = newUser(ID_USER_ME);
        user.setMe(true);
        return user;
    }

    private List<User> onePersonList() {
        List<User> users = new ArrayList<>();
        users.add(newUser(ID_USER_1));
        return users;
    }

    private User newUser(Long id) {
        User user = new User();
        user.setIdUser(id);
        return user;
    }
}