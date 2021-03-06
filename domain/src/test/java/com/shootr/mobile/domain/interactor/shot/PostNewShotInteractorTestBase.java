package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.model.shot.BaseMessage;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.service.MessageSender;
import java.io.File;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class PostNewShotInteractorTestBase {

  public static final Long CURRENT_USER_ID = 1L;

  public static final String COMMENT_EMPTY = "";
  public static final String COMMENT_STUB = "comment";
  public static final File IMAGE_NULL = null;
  public static final File IMAGE_STUB = new File(".");
  public static final String STREAM_TITLE_STUB = "title";

  @Mock SessionRepository sessionRepository;
  @Mock MessageSender messageSender;

  protected abstract PostNewMessageInteractor getInteractorForCommonTests();

  @Test public void shouldSendShotWithCurrentUserInfo() throws Exception {
    setupCurrentUserSession();

    getInteractorForCommonTests().postNewBaseMessage(COMMENT_STUB, IMAGE_NULL, true, new DummyCallback(),
        new DummyErrorCallback());

    ArgumentCaptor<Shot> shotArgumentCaptor = ArgumentCaptor.forClass(Shot.class);
    verify(messageSender).sendMessage(shotArgumentCaptor.capture(), any(File.class));
    Shot publishedShot = shotArgumentCaptor.getValue();
    BaseMessage.BaseMessageUserInfo userInfo = publishedShot.getUserInfo();
    assertUserInfoIsFromUser(userInfo, currentUser());
  }

  @Test public void shouldSendNullCommentWhenInputCommentIsEmpty() throws Exception {
    setupCurrentUserSession();

    getInteractorForCommonTests().postNewBaseMessage(COMMENT_EMPTY, IMAGE_NULL, true, new DummyCallback(),
        new DummyErrorCallback());

    ArgumentCaptor<Shot> shotArgumentCaptor = ArgumentCaptor.forClass(Shot.class);
    verify(messageSender).sendMessage(shotArgumentCaptor.capture(), any(File.class));
    Shot publishedShot = shotArgumentCaptor.getValue();
    assertThat(publishedShot.getComment()).isNull();
  }

  @Test public void shouldSendShotThroughDispatcher() throws Exception {
    setupCurrentUserSession();

    getInteractorForCommonTests().postNewBaseMessage(COMMENT_STUB, IMAGE_NULL, true, new DummyCallback(),
        new DummyErrorCallback());

    verify(messageSender, times(1)).sendMessage(any(Shot.class), any(File.class));
  }

  //region Assertion
  private void assertUserInfoIsFromUser(BaseMessage.BaseMessageUserInfo userInfo, User user) {
    assertThat(userInfo.getIdUser()).isEqualTo(user.getIdUser());
    assertThat(userInfo.getUsername()).isEqualTo(user.getUsername());
    assertThat(userInfo.getAvatar()).isEqualTo(user.getPhoto());
  }

  protected void assertStreamInfoIsFromStream(Shot.ShotStreamInfo streamInfo, Stream stream) {
    assertThat(streamInfo.getIdStream()).isEqualTo(stream.getId());
    assertThat(streamInfo.getStreamTitle()).isEqualTo(stream.getTitle());
  }
  //endregion

  //region Setup
  protected void setupCurrentUserSession() {
    when(sessionRepository.getCurrentUser()).thenReturn(currentUser());
    when(sessionRepository.getCurrentUserId()).thenReturn(currentUser().getIdUser());
  }
  //endregion

  //region Stubs
  protected User currentUser() {
    User user = new User();
    user.setIdUser(String.valueOf(CURRENT_USER_ID));
    user.setUsername("currentUsername");
    user.setPhoto("http://avatar.jpg");
    return user;
  }

  protected Shot captureSentShot() {
    ArgumentCaptor<Shot> shotCaptor = ArgumentCaptor.forClass(Shot.class);
    verify(messageSender).sendMessage(shotCaptor.capture(), any(File.class));
    return shotCaptor.getValue();
  }
  //endregion

  protected class DummyCallback implements PostNewMessageInteractor.CompletedCallback {

    @Override public void onCompleted() {
            /* no-op */
    }
  }

  protected class DummyErrorCallback implements Interactor.ErrorCallback {

    @Override public void onError(ShootrException error) {
    }
  }
}
