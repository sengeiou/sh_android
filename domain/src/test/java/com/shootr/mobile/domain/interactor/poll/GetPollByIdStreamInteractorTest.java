package com.shootr.mobile.domain.interactor.poll;

import com.shootr.mobile.domain.Poll;
import com.shootr.mobile.domain.PollOption;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.PollRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetPollByIdStreamInteractorTest {

  public static final String ID_STREAM = "ID_STREAM";
  private GetPollByIdStreamInteractor interactor;
  @Mock PollRepository localPollRepository;
  @Mock PollRepository remotePollRepository;
  @Mock Interactor.Callback<Poll> callback;
  @Mock Interactor.ErrorCallback errorCallback;
  @Captor ArgumentCaptor<Poll> pollArgumentCaptor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    interactor =
        new GetPollByIdStreamInteractor(interactorHandler, postExecutionThread, localPollRepository,
            remotePollRepository);
  }

  @Test public void shouldGetPollFromLocal() throws Exception {
    when(localPollRepository.getPollByIdStream(anyString())).thenReturn(poll());
    when(remotePollRepository.getPollByIdStream(anyString())).thenReturn(poll());

    interactor.loadPoll(ID_STREAM, callback, errorCallback);

    verify(localPollRepository).getPollByIdStream(anyString());
  }

  @Test public void shouldGetPollFromRemote() throws Exception {
    when(localPollRepository.getPollByIdStream(anyString())).thenReturn(poll());
    when(remotePollRepository.getPollByIdStream(anyString())).thenReturn(poll());

    interactor.loadPoll(ID_STREAM, callback, errorCallback);

    verify(remotePollRepository).getPollByIdStream(anyString());
  }

  @Test public void shouldNotifyLoadedPoll() throws Exception {
    when(localPollRepository.getPollByIdStream(anyString())).thenReturn(poll());
    when(remotePollRepository.getPollByIdStream(anyString())).thenReturn(poll());

    interactor.loadPoll(ID_STREAM, callback, errorCallback);

    verify(callback, times(2)).onLoaded(any(Poll.class));
  }

  @Test public void shouldNotifyErrorWhenRemoteRepositoryThrowsException() throws Exception {
    when(localPollRepository.getPollByIdStream(anyString())).thenReturn(poll());
    when(remotePollRepository.getPollByIdStream(ID_STREAM)).thenThrow(
        new ServerCommunicationException(new Throwable()));

    interactor.loadPoll(ID_STREAM, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  @Test public void shouldLoadPollWithOptionsOrdered() throws Exception {
    when(localPollRepository.getPollByIdStream(anyString())).thenReturn(poll());
    when(remotePollRepository.getPollByIdStream(anyString())).thenReturn(poll());

    interactor.loadPoll(ID_STREAM, callback, errorCallback);

    verify(callback, times(2)).onLoaded(pollArgumentCaptor.capture());
    assertThat(pollArgumentCaptor.getValue().getPollOptions()).isSortedAccordingTo(
        PollOption.PollOptionComparator);
  }

  @Test public void shouldNotifyNullWhenReceivingNoPoll() throws Exception {
    when(localPollRepository.getPollByIdStream(anyString())).thenReturn(nopoll());
    when(remotePollRepository.getPollByIdStream(anyString())).thenReturn(nopoll());

    interactor.loadPoll(ID_STREAM, callback, errorCallback);

    verify(callback, times(2)).onLoaded(null);
  }

  private List<Poll> nopoll() {
    return null;
  }

  private List<Poll> poll() {
    Poll poll = new Poll();
    poll.setPollOptions(pollOptions());
    return Collections.singletonList(poll);
  }

  private List<PollOption> pollOptions() {
    PollOption pollOption = new PollOption();
    pollOption.setOrder(1);
    PollOption otherPollOption = new PollOption();
    otherPollOption.setOrder(0);
    return Arrays.asList(pollOption, otherPollOption);
  }
}
