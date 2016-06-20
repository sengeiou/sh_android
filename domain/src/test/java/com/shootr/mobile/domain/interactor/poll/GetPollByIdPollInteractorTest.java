package com.shootr.mobile.domain.interactor.poll;

import com.shootr.mobile.domain.Poll;
import com.shootr.mobile.domain.PollOption;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.UserCannotVoteRequestException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.PollRepository;
import java.util.Arrays;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetPollByIdPollInteractorTest {

  private static final String ID_POLL = "idPoll";
  @Mock PollRepository localPollRepository;
  @Mock PollRepository remotePollRepository;
  @Mock Interactor.Callback<Poll> callback;
  @Mock Interactor.ErrorCallback errorCallback;
  @Captor ArgumentCaptor<Poll> pollArgumentCaptor;

  private GetPollByIdPollInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    interactor =
        new GetPollByIdPollInteractor(interactorHandler, postExecutionThread, localPollRepository,
            remotePollRepository);
  }

  @Test public void shouldGetPollFromLocal() throws Exception {
    doThrow(ServerCommunicationException.class).when(remotePollRepository)
        .getPollByIdPoll(anyString());
    when(localPollRepository.getPollByIdPoll(anyString())).thenReturn(poll());

    interactor.loadPollByIdPoll(ID_POLL, callback, errorCallback);

    verify(localPollRepository).getPollByIdPoll(anyString());
  }

  @Test public void shouldGetPollFromRemote() throws Exception {
    when(localPollRepository.getPollByIdPoll(anyString())).thenReturn(poll());
    when(remotePollRepository.getPollByIdPoll(anyString())).thenReturn(poll());

    interactor.loadPollByIdPoll(ID_POLL, callback, errorCallback);

    verify(remotePollRepository).getPollByIdPoll(anyString());
  }

  @Test public void shouldNotifyLoadedPoll() throws Exception {
    when(localPollRepository.getPollByIdPoll(anyString())).thenReturn(poll());
    when(remotePollRepository.getPollByIdPoll(anyString())).thenReturn(poll());

    interactor.loadPollByIdPoll(ID_POLL, callback, errorCallback);

    verify(callback).onLoaded(any(Poll.class));
  }

  @Test public void shouldNotifyErrorWhenRemoteRepositoryThrowsException() throws Exception {
    when(localPollRepository.getPollByIdPoll(anyString())).thenReturn(poll());
    when(remotePollRepository.getPollByIdPoll(ID_POLL)).thenThrow(
        new ServerCommunicationException(new Throwable()));

    interactor.loadPollByIdPoll(ID_POLL, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  @Test public void shouldNotifyUserCannotVoteErrorWhenThrowsVoteException() throws Exception {
    when(localPollRepository.getPollByIdPoll(anyString())).thenReturn(poll());
    when(remotePollRepository.getPollByIdPoll(ID_POLL)).thenThrow(
        new UserCannotVoteRequestException(new Throwable()));

    interactor.loadPollByIdPoll(ID_POLL, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  @Test public void shouldLoadPollWithOptionsOrdered() throws Exception {
    when(localPollRepository.getPollByIdPoll(anyString())).thenReturn(poll());
    when(remotePollRepository.getPollByIdPoll(anyString())).thenReturn(poll());

    interactor.loadPollByIdPoll(ID_POLL, callback, errorCallback);

    verify(callback).onLoaded(pollArgumentCaptor.capture());
    assertThat(pollArgumentCaptor.getValue().getPollOptions()).isSortedAccordingTo(
        PollOption.PollOptionComparator);
  }

  @Test public void shouldNotifyNullWhenReceivingNoPoll() throws Exception {
    when(localPollRepository.getPollByIdPoll(anyString())).thenReturn(nopoll());
    when(remotePollRepository.getPollByIdPoll(anyString())).thenReturn(nopoll());

    interactor.loadPollByIdPoll(ID_POLL, callback, errorCallback);

    verify(callback).onLoaded(null);
  }

  private Poll nopoll() {
    return null;
  }

  private Poll poll() {
    Poll poll = new Poll();
    poll.setIdPoll(ID_POLL);
    poll.setPollOptions(pollOptions());
    return poll;
  }

  private List<PollOption> pollOptions() {
    PollOption pollOption = new PollOption();
    pollOption.setOrder(1);
    PollOption otherPollOption = new PollOption();
    otherPollOption.setOrder(0);
    return Arrays.asList(pollOption, otherPollOption);
  }
}
