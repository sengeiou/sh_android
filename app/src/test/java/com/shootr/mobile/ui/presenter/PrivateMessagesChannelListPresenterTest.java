package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.timeline.privateMessage.GetPrivateMessagesChannelsInteractor;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageChannel;
import com.shootr.mobile.ui.model.mappers.PrivateMessageChannelModelMapper;
import com.shootr.mobile.ui.views.PrivateMessageChannelListView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class PrivateMessagesChannelListPresenterTest {

  @Mock GetPrivateMessagesChannelsInteractor getPrivateMessagesChannelsInteractor;
  @Mock ErrorMessageFactory errorMessageFactory;
  @Mock PrivateMessageChannelListView view;
  @Mock Bus bus;
  @Mock BusPublisher busPublisher;

  private PrivateMessagesChannelListPresenter presenter;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    PrivateMessageChannelModelMapper mapper = new PrivateMessageChannelModelMapper();

    presenter =
        new PrivateMessagesChannelListPresenter(getPrivateMessagesChannelsInteractor, mapper,
            errorMessageFactory, busPublisher, bus);
  }

  @Test public void shouldRenderChannelsWhenInitializes() throws Exception {
    setupMessagesChannelInteractor();

    presenter.initialize(view);

    verify(view).renderChannels(anyList());
  }

  @Test public void shouldShowEmptyWhenInitializesAndInteractorReturnsEmptyList() throws Exception {
    setupEmptyMessagesChannelInteractor();

    presenter.initialize(view);

    verify(view).showEmpty();
  }

  private void setupMessagesChannelInteractor() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<List<PrivateMessageChannel>> callback =
            (Interactor.Callback<List<PrivateMessageChannel>>) invocation.getArguments()[0];
        callback.onLoaded(privateMessageChannels());
        return null;
      }
    }).when(getPrivateMessagesChannelsInteractor)
        .loadChannels(any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
  }

  private void setupEmptyMessagesChannelInteractor() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<List<PrivateMessageChannel>> callback =
            (Interactor.Callback<List<PrivateMessageChannel>>) invocation.getArguments()[0];
        callback.onLoaded(Collections.EMPTY_LIST);
        return null;
      }
    }).when(getPrivateMessagesChannelsInteractor)
        .loadChannels(any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
  }

  private List<PrivateMessageChannel> privateMessageChannels() {
    PrivateMessageChannel privateMessageChannel = new PrivateMessageChannel();
    return Collections.singletonList(privateMessageChannel);
  }
}
