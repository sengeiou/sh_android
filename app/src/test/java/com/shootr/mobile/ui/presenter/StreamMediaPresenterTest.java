package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.GetOlderStreamMediaInteractor;
import com.shootr.mobile.domain.interactor.shot.GetStreamMediaInteractor;
import com.shootr.mobile.domain.model.shot.BaseMessage;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.shootr.mobile.ui.views.StreamMediaView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class StreamMediaPresenterTest {

  private static final String STREAM_ID = "streamId";
  private static final Integer COUNT = 100;
  private static final String SHOT_ID = "shotId";
  private static final String USER_ID = "userId";
  private static final String USERNAME = "username";
  @Mock GetStreamMediaInteractor getStreamMediaInteractor;
  @Mock GetOlderStreamMediaInteractor getOlderStreamMediaInteractor;
  @Mock ErrorMessageFactory errorMessageFactory;
  @Mock StreamMediaView streamMediaView;

  private StreamMediaPresenter presenter;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    ShotModelMapper shotModelMapper = new ShotModelMapper();
    presenter = new StreamMediaPresenter(getStreamMediaInteractor, getOlderStreamMediaInteractor,
        shotModelMapper, errorMessageFactory);
    presenter.setView(streamMediaView);
  }

  @Test public void shouldSetMediaWhenInitialize() throws Exception {
    setupGetStreamMediaInteractorCallback();

    presenter.initialize(streamMediaView, STREAM_ID, COUNT);

    verify(streamMediaView).setMedia(anyList());
  }

  @Test public void shouldShowErrorWhenGetStreamMediaInteractorThrowsError() throws Exception {
    setupGetStreamMediaInteractorErrorCallback();

    presenter.initialize(streamMediaView, STREAM_ID, COUNT);

    verify(streamMediaView).showError(anyString());
  }

  @Test public void shouldAddOldMediaWhenLoadAllMedia() throws Exception {
    setupGetOlderStreamMediaInteractorCallback();

    presenter.showingLastMedia(shotModel());

    verify(streamMediaView).addOldMedia(anyList());
  }

  @Test public void shouldShowNoMoreMediaWhenGetOlderStreamMediaAndReturnsEmptyList()
      throws Exception {
    setupGetOlderStreamMediaEmptyListInteractorCallback();

    presenter.showingLastMedia(shotModel());

    verify(streamMediaView).showNoMoreMedia();
  }

  @Test public void shouldShowErrorWhenGetOlderStreamMediaReturnsError() throws Exception {
    setupGetOlderStreamMediaInteractorErrorCallback();

    presenter.showingLastMedia(shotModel());

    verify(streamMediaView).showError(anyString());
  }

  private void setupGetOlderStreamMediaInteractorErrorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.ErrorCallback errorCallback =
            (Interactor.ErrorCallback) invocation.getArguments()[3];
        errorCallback.onError(new ShootrException() {
        });
        return null;
      }
    }).when(getOlderStreamMediaInteractor)
        .getOlderStreamMedia(anyString(), anyLong(), any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
  }

  private void setupGetOlderStreamMediaEmptyListInteractorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<List<Shot>> callback =
            (Interactor.Callback<List<Shot>>) invocation.getArguments()[2];
        callback.onLoaded(new ArrayList<Shot>());
        return null;
      }
    }).when(getOlderStreamMediaInteractor)
        .getOlderStreamMedia(anyString(), anyLong(), any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
  }

  private void setupGetOlderStreamMediaInteractorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<List<Shot>> callback =
            (Interactor.Callback<List<Shot>>) invocation.getArguments()[2];
        callback.onLoaded(shots());
        return null;
      }
    }).when(getOlderStreamMediaInteractor)
        .getOlderStreamMedia(anyString(), anyLong(), any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
  }

  private void setupGetStreamMediaInteractorErrorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.ErrorCallback errorCallback =
            (Interactor.ErrorCallback) invocation.getArguments()[2];
        errorCallback.onError(any(ShootrException.class));
        return null;
      }
    }).when(getStreamMediaInteractor)
        .getStreamMedia(anyString(), any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
  }

  private void setupGetStreamMediaInteractorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<List<Shot>> callback =
            (Interactor.Callback<List<Shot>>) invocation.getArguments()[1];
        callback.onLoaded(shots());
        return null;
      }
    }).when(getStreamMediaInteractor)
        .getStreamMedia(anyString(), any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
  }

  private List<Shot> shots() {
    BaseMessage.BaseMessageUserInfo baseMessageUserInfo = new BaseMessage.BaseMessageUserInfo();
    baseMessageUserInfo.setIdUser(USER_ID);
    baseMessageUserInfo.setUsername(USERNAME);

    Shot shot = new Shot();
    shot.setIdShot(SHOT_ID);
    shot.setUserInfo(baseMessageUserInfo);
    return Collections.singletonList(shot);
  }

  private ShotModel shotModel() {
    ShotModel shotModel = new ShotModel();
    shotModel.setBirth(new Date());
    return shotModel;
  }
}
