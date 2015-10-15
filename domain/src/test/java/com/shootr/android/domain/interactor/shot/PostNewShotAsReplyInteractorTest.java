package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.ShotType;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.ShotRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class PostNewShotAsReplyInteractorTest extends PostNewShotInteractorTestBase{

    private static final String PARENT_SHOT_ID = "1L";
    private static final Long PARENT_STREAM_ID = 2L;
    private static final String PARENT_STREAM_TITLE = "title";
    private static final String PARENT_STREAM_SHORT_TITLE = "shortTitle";
    private static final Long PARENT_SHOT_USER_ID = 3L;
    private static final String PARENT_SHOT_USERNAME = "parent username";

    @Mock ShotRepository localShotRepository;
    @Mock ShotRepository remoteShotRepository;

    private PostNewShotAsReplyInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        interactor = new PostNewShotAsReplyInteractor(postExecutionThread,
          interactorHandler,
          sessionRepository,
          shotSender, localShotRepository, remoteShotRepository);
    }

    @Override protected PostNewShotInteractor getInteractorForCommonTests() {
        setupParentShot();
        return interactor;
    }

    @Test public void shouldHaveParentStreamInfo() throws Exception {
        setupCurrentUserSession();
        setupParentShot();

        interactor.postNewShotAsReply(COMMENT_STUB,
          IMAGE_STUB, PARENT_SHOT_ID,
          new DummyCallback(),
          new DummyErrorCallback());

        Shot sentShot = captureSentShot();
        assertThat(sentShot.getStreamInfo()).isEqualTo(parentStreamInfo());
    }

    @Test public void shouldHaveParentShotId() throws Exception {
        setupCurrentUserSession();
        setupParentShot();

        interactor.postNewShotAsReply(COMMENT_STUB,
          IMAGE_STUB, PARENT_SHOT_ID,
          new DummyCallback(),
          new DummyErrorCallback());

        Shot sentShot = captureSentShot();
        assertThat(sentShot.getParentShotId()).isEqualTo(PARENT_SHOT_ID);
    }

    @Test public void shouldHaveParentShotIdEvenIfNoParentShotInLocal() throws Exception {
        setupCurrentUserSession();
        setupParentShotFromRemote();

        interactor.postNewShotAsReply(COMMENT_STUB,
          IMAGE_STUB, PARENT_SHOT_ID,
          new DummyCallback(),
          new DummyErrorCallback());

        Shot sentShot = captureSentShot();
        assertThat(sentShot.getParentShotId()).isEqualTo(PARENT_SHOT_ID);
    }

    @Test public void shouldHaveParentUserId() throws Exception {
        setupCurrentUserSession();
        setupParentShot();

        interactor.postNewShotAsReply(COMMENT_STUB,
          IMAGE_STUB,
          PARENT_SHOT_ID,
          new DummyCallback(),
          new DummyErrorCallback());

        Shot sentShot = captureSentShot();
        assertThat(sentShot.getParentShotUserId()).isEqualTo(String.valueOf(PARENT_SHOT_USER_ID));
    }

    @Test public void shouldHaveParentUsername() throws Exception {
        setupCurrentUserSession();
        setupParentShot();

        interactor.postNewShotAsReply(COMMENT_STUB,
          IMAGE_STUB, PARENT_SHOT_ID,
          new DummyCallback(),
          new DummyErrorCallback());

        Shot sentShot = captureSentShot();
        assertThat(sentShot.getParentShotUsername()).isEqualTo(PARENT_SHOT_USERNAME);
    }

    private Shot.ShotStreamInfo parentStreamInfo() {
        Shot.ShotStreamInfo shotStreamInfo = new Shot.ShotStreamInfo();
        shotStreamInfo.setIdStream(String.valueOf(PARENT_STREAM_ID));
        shotStreamInfo.setStreamTitle(PARENT_STREAM_TITLE);
        shotStreamInfo.setStreamShortTitle(PARENT_STREAM_SHORT_TITLE);
        return shotStreamInfo;
    }

    private void setupParentShot() {
        when(localShotRepository.getShot(anyString())).thenReturn(parentShot());
    }

    private void setupParentShotFromRemote() {
        when(localShotRepository.getShot(anyString())).thenReturn(null);
        when(remoteShotRepository.getShot(anyString())).thenReturn(parentShot());
    }

    private Shot parentShot() {
        Shot shot = new Shot();
        shot.setIdShot(PARENT_SHOT_ID);
        shot.setStreamInfo(parentStreamInfo());
        shot.setUserInfo(parentUserInfo());
        shot.setType(ShotType.COMMENT);
        return shot;
    }

    private Shot.ShotUserInfo parentUserInfo() {
        Shot.ShotUserInfo shotUserInfo = new Shot.ShotUserInfo();
        shotUserInfo.setIdUser(String.valueOf(PARENT_SHOT_USER_ID));
        shotUserInfo.setUsername(PARENT_SHOT_USERNAME);
        return shotUserInfo;
    }
}