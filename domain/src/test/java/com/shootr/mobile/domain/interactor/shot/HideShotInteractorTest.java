package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.ShotType;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.ShotRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HideShotInteractorTest {

    private static final String SHOT_ID ="shot_id" ;
    private static final Long STREAM_ID = 2L;
    private static final String STREAM_TITLE = "title";
    private static final String STREAM_SHORT_TITLE = "shortTitle";
    private static final Long SHOT_USER_ID = 3L;
    private static final String SHOT_USERNAME = "username";

    private HideShotInteractor hideShotInteractor;
    @Mock ShotRepository localShotRepository;
    @Mock ShotRepository remoteShotRepository;
    @Mock Interactor.CompletedCallback callback;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        TestInteractorHandler interactorHandler = new TestInteractorHandler();
        TestPostExecutionThread postExecutionThread = new TestPostExecutionThread();
        hideShotInteractor= new HideShotInteractor(interactorHandler, postExecutionThread,
          localShotRepository,remoteShotRepository);
    }

    @Test public void shouldNotifyCallbackAfterSendingToServer() throws Exception {
        setupLocalShot();

        hideShotInteractor.hideShot(SHOT_ID, callback);

        InOrder inOrder = inOrder(callback, remoteShotRepository);
        inOrder.verify(remoteShotRepository).hideShot(SHOT_ID);
        inOrder.verify(callback).onCompleted();
    }

    @Test public void shouldNotifyCallbackWhenServiceFails() throws Exception {
        setupLocalShot();
        doThrow(new ServerCommunicationException(null)).when(remoteShotRepository).hideShot(anyString());

        hideShotInteractor.hideShot(SHOT_ID, callback);

        verify(callback).onCompleted();
    }

    private void setupLocalShot() {
        when(localShotRepository.getShot(anyString())).thenReturn(shot());
    }

    private Shot shot() {
        Shot shot = new Shot();
        shot.setIdShot(SHOT_ID);
        shot.setStreamInfo(streamInfo());
        shot.setUserInfo(userInfo());
        shot.setType(ShotType.COMMENT);
        shot.setNiceCount(0);
        return shot;
    }

    private Shot.ShotStreamInfo streamInfo() {
        Shot.ShotStreamInfo shotStreamInfo = new Shot.ShotStreamInfo();
        shotStreamInfo.setIdStream(String.valueOf(STREAM_ID));
        shotStreamInfo.setStreamTitle(STREAM_TITLE);
        shotStreamInfo.setStreamShortTitle(STREAM_SHORT_TITLE);
        return shotStreamInfo;
    }

    private Shot.ShotUserInfo userInfo() {
        Shot.ShotUserInfo shotUserInfo = new Shot.ShotUserInfo();
        shotUserInfo.setIdUser(String.valueOf(SHOT_USER_ID));
        shotUserInfo.setUsername(SHOT_USERNAME);
        return shotUserInfo;
    }
}
