package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageChannel;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageTimeline;
import com.shootr.mobile.domain.model.shot.BaseMessage;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.Poller;
import com.shootr.mobile.ui.model.PrivateMessageModel;
import com.shootr.mobile.ui.model.mappers.PrivateMessageChannelModelMapper;
import com.shootr.mobile.ui.model.mappers.PrivateMessageModelMapper;
import com.shootr.mobile.ui.presenter.interactorwrapper.PrivateMessageChannelTimelineInteractorWrapper;
import com.shootr.mobile.ui.views.PrivateMessageChannelTimelineView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import java.util.Collections;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class PrivateMessageTimelinePresenterTest {

  private static final String ID_CHANNEL = "idChannel";
  private static final String ID_TARGET_USER = "idTargetUser";

  @Mock PrivateMessageChannelTimelineInteractorWrapper interactorWrapper;
  @Mock Bus bus;
  @Mock ErrorMessageFactory errorMessageFactory;
  @Mock Poller poller;
  @Mock SessionRepository sessionRepository;
  @Mock PrivateMessageChannelTimelineView view;

  private PrivateMessageTimelinePresenter presenter;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    PrivateMessageModelMapper privateMessageModelMapper =
        new PrivateMessageModelMapper(sessionRepository);
    PrivateMessageChannelModelMapper privateMessageChannelModelMapper =
        new PrivateMessageChannelModelMapper();

    presenter =
        new PrivateMessageTimelinePresenter(interactorWrapper, bus, errorMessageFactory, poller,
            privateMessageModelMapper, privateMessageChannelModelMapper);
  }

  @Test public void shouldShowMessagesWhenInitializesTimeline() throws Exception {
    setupLoadTimelineInteractorCallbacks();

    presenter.setIsFirstLoad(true);
    presenter.initialize(view, ID_CHANNEL, ID_TARGET_USER);

    verify(view).showShots();
  }

  @Test public void shouldUpdateMessagesInfoWhenRefreshTimelineAndNoMoreMessages()
      throws Exception {
    setupLoadTimelineInteractorCallbacks();
    presenter.initialize(view, ID_CHANNEL, ID_TARGET_USER);

    presenter.loadTimeline();

    verify(view, atLeastOnce()).updateMessagesInfo(anyList());
  }

  @Test public void shouldShowOldMessagesInViewWhenIsShowingTheLastMessage() throws Exception {
    setupLoadTimelineInteractorCallbacks();
    setupLoadOldMessagesInteractorCallbacks();
    presenter.initialize(view, ID_CHANNEL, ID_TARGET_USER);

    presenter.showingLastMessage(privateMessageModel());

    verify(view, atLeastOnce()).addOldMessages(anyList());
  }

  private void setupLoadTimelineInteractorCallbacks() {
    doAnswer(new Answer<Void>() {
      @Override public Void answer(InvocationOnMock invocation) throws Throwable {
        ((Interactor.Callback<PrivateMessageTimeline>) invocation.getArguments()[3]).onLoaded(
            privateMessageTimeline());
        return null;
      }
    }).when(interactorWrapper)
        .loadTimeline(anyString(), anyString(), anyBoolean(), any(Interactor.Callback.class));
  }

  private void setupLoadOldMessagesInteractorCallbacks() {
    doAnswer(new Answer<Void>() {
      @Override public Void answer(InvocationOnMock invocation) throws Throwable {
        ((Interactor.Callback<PrivateMessageTimeline>) invocation.getArguments()[2]).onLoaded(
            privateMessageTimeline());
        return null;
      }
    }).when(interactorWrapper)
        .obtainOlderTimeline(anyString(), anyLong(), any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
  }

  private PrivateMessageTimeline privateMessageTimeline() {
    PrivateMessageTimeline timeline = new PrivateMessageTimeline();
    timeline.setPrivateMessageChannel(new PrivateMessageChannel());
    timeline.setPrivateMessages(Collections.singletonList(privateMessage()));
    return timeline;
  }

  private PrivateMessage privateMessage() {
    PrivateMessage privateMessage = new PrivateMessage();
    privateMessage.setPublishDate(new Date());
    BaseMessage.BaseMessageUserInfo baseMessageUserInfo = new BaseMessage.BaseMessageUserInfo();
    baseMessageUserInfo.setIdUser(ID_TARGET_USER);
    privateMessage.setUserInfo(baseMessageUserInfo);
    return privateMessage;
  }

  private PrivateMessageModel privateMessageModel() {
    PrivateMessageModel privateMessage = new PrivateMessageModel();
    privateMessage.setPublishDate(new Date());
    privateMessage.setBirth(new Date());
    return privateMessage;
  }
}
