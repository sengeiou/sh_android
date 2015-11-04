package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetLastShotsInteractorTest {

    private static final String ID_CURRENT_USER = "current";
    private static final String ID_FOLLOWING = "following";
    private static final String ID_NOT_FOLLOWING = "not_following";

    @Mock UserRepository localUserRepository;
    @Mock com.shootr.mobile.domain.repository.ShotRepository localShotRepository;
    @Mock com.shootr.mobile.domain.repository.ShotRepository remoteShotRepository;
    @Mock Interactor.Callback<List<com.shootr.mobile.domain.Shot>> callback;
    @Mock Interactor.ErrorCallback errorCallback;
    @Mock com.shootr.mobile.domain.repository.SessionRepository sessionRepository;

    private GetLastShotsInteractor interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        interactor = new GetLastShotsInteractor(new TestInteractorHandler(),
          new TestPostExecutionThread(),
          localShotRepository,
          remoteShotRepository,
          sessionRepository,
          localUserRepository);

        when(sessionRepository.getCurrentUserId()).thenReturn(ID_CURRENT_USER);
        when(localUserRepository.isFollowing(ID_FOLLOWING)).thenReturn(true);
        when(localUserRepository.isFollowing(ID_NOT_FOLLOWING)).thenReturn(false);
    }

    @Test
    public void shouldPutRemoteShotsInLocalWhenCurrentUser() throws Exception {
        List<com.shootr.mobile.domain.Shot> remoteShots = shotList();
        when(remoteShotRepository.getShotsFromUser(eq(ID_CURRENT_USER), anyInt())).thenReturn(remoteShots);

        interactor.loadLastShots(ID_CURRENT_USER, callback, errorCallback);

        verify(localShotRepository).putShots(remoteShots);
    }

    @Test
    public void shouldPutRemoteShotsInLocalWhenFollowing() throws Exception {
        List<com.shootr.mobile.domain.Shot> remoteShots = shotList();
        when(remoteShotRepository.getShotsFromUser(eq(ID_FOLLOWING), anyInt())).thenReturn(remoteShots);

        interactor.loadLastShots(ID_FOLLOWING, callback, errorCallback);

        verify(localShotRepository).putShots(remoteShots);
    }

    @Test
    public void shouldNotPutRemoteShotsInLocalWhenNotFollowing() throws Exception {
        List<com.shootr.mobile.domain.Shot> remoteShots = shotList();
        when(remoteShotRepository.getShotsFromUser(eq(ID_NOT_FOLLOWING), anyInt())).thenReturn(remoteShots);

        interactor.loadLastShots(ID_NOT_FOLLOWING, callback, errorCallback);

        verify(localShotRepository, never()).putShots(remoteShots);
    }

    private List<com.shootr.mobile.domain.Shot> shotList() {
        return Collections.singletonList(shot());
    }

    private com.shootr.mobile.domain.Shot shot() {
        com.shootr.mobile.domain.Shot shot = new com.shootr.mobile.domain.Shot();
        shot.setIdShot("shot");
        return shot;
    }
}