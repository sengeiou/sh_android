package com.shootr.mobile.domain.interactor.poll;

import com.shootr.mobile.domain.model.poll.Poll;
import com.shootr.mobile.domain.model.poll.PollOption;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.UserCannotVoteRequestException;
import com.shootr.mobile.domain.exception.UserHasVotedRequestException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.poll.InternalPollRepository;
import com.shootr.mobile.domain.repository.poll.ExternalPollRepository;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VotePollOptionInteractorTest {
  public static final String ID_POLL = "id_poll";
  public static final String ID_POLL_OPTION = "id_poll_option";
  private static final String ANOTHER_ID_POLL_OPTION = "another_id_poll_option";
  private VotePollOptionInteractor votePollOptionInteractor;
  @Mock InternalPollRepository localPollRepository;
  @Mock ExternalPollRepository remotePollRepository;
  @Mock Interactor.Callback<Poll> callback;
  @Mock Interactor.ErrorCallback errorCallback;
  @Captor ArgumentCaptor<Poll> pollArgumentCaptor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    votePollOptionInteractor =
        new VotePollOptionInteractor(interactorHandler, postExecutionThread, localPollRepository,
            remotePollRepository);
  }

  @Test public void shouldNotPutPollIfLocalReturnsNoPollOptionsWhenFallbackToLocal()
      throws Exception {
    when(remotePollRepository.vote(ID_POLL, ID_POLL_OPTION)).thenThrow(
        new ServerCommunicationException(new Throwable()));
    when(localPollRepository.getPollByIdPoll(ID_POLL)).thenReturn(pollWithoutPollOptions());

    votePollOptionInteractor.vote(ID_POLL, ID_POLL_OPTION, callback, errorCallback);

    verify(localPollRepository, never()).putPoll(any(Poll.class));
  }

  @Test public void shoudlNotifyErrorWhenServerCommunticationExceptionThrown() throws Exception {
    when(remotePollRepository.vote(ID_POLL, ID_POLL_OPTION)).thenThrow(
        new ServerCommunicationException(new Throwable()));
    when(localPollRepository.getPollByIdPoll(ID_POLL)).thenReturn(pollWithPollOptions());

    votePollOptionInteractor.vote(ID_POLL, ID_POLL_OPTION, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  @Test
  public void shouldVoteInRemoteRepositoryWhenVote() throws Exception {
    when(remotePollRepository.vote(ID_POLL, ID_POLL_OPTION)).thenReturn(pollWithPollOptions());

    votePollOptionInteractor.vote(ID_POLL, ID_POLL_OPTION, callback, errorCallback);

    verify(remotePollRepository).vote(ID_POLL, ID_POLL_OPTION);
  }

  @Test
  public void shouldNotifyPollLoadedWhenVote() throws Exception {
    when(remotePollRepository.vote(ID_POLL, ID_POLL_OPTION)).thenReturn(pollWithoutPollOptions());

    votePollOptionInteractor.vote(ID_POLL, ID_POLL_OPTION, callback, errorCallback);

    verify(callback).onLoaded(any(Poll.class));
  }

  @Test
  public void shouldGetLocalPollWhenUserCannotVoteRequestExceptionThrown() throws Exception {
    when(remotePollRepository.vote(ID_POLL, ID_POLL_OPTION)).thenThrow(
        new UserCannotVoteRequestException(new Throwable()));
    when(localPollRepository.getPollByIdPoll(ID_POLL)).thenReturn(pollWithPollOptions());

    votePollOptionInteractor.vote(ID_POLL, ID_POLL_OPTION, callback, errorCallback);

    verify(localPollRepository).getPollByIdPoll(ID_POLL);
  }

  @Test
  public void shouldNotifyLocalPollWhenUserCannotVoteRequestExceptionThrown() throws Exception {
    when(remotePollRepository.vote(ID_POLL, ID_POLL_OPTION)).thenThrow(
        new UserCannotVoteRequestException(new Throwable()));
    when(localPollRepository.getPollByIdPoll(ID_POLL)).thenReturn(pollWithPollOptions());

    votePollOptionInteractor.vote(ID_POLL, ID_POLL_OPTION, callback, errorCallback);

    verify(callback).onLoaded(any(Poll.class));
  }

  @Test
  public void shouldGetLocalPollWhenUserHasVotedRequestExceptionThrown() throws Exception {
    when(remotePollRepository.vote(ID_POLL, ID_POLL_OPTION)).thenThrow(
        new UserHasVotedRequestException(new Throwable()));
    when(localPollRepository.getPollByIdPoll(ID_POLL)).thenReturn(pollWithPollOptions());

    votePollOptionInteractor.vote(ID_POLL, ID_POLL_OPTION, callback, errorCallback);

    verify(localPollRepository).getPollByIdPoll(ID_POLL);
  }

  @Test
  public void shouldNotifyLocalPollWhenUserHasVotedRequestExceptionThrown() throws Exception {
    when(remotePollRepository.vote(ID_POLL, ID_POLL_OPTION)).thenThrow(
        new UserHasVotedRequestException(new Throwable()));
    when(localPollRepository.getPollByIdPoll(ID_POLL)).thenReturn(pollWithPollOptions());

    votePollOptionInteractor.vote(ID_POLL, ID_POLL_OPTION, callback, errorCallback);

    verify(callback).onLoaded(any(Poll.class));
  }

  private Poll pollWithoutPollOptions() {
    Poll poll = new Poll();
    poll.setPollOptions(Collections.<PollOption>emptyList());
    return poll;
  }

  private Poll pollWithPollOptions() {
    Poll poll = new Poll();
    PollOption option = new PollOption();
    option.setIdPollOption(ID_POLL_OPTION);
    option.setVotes(1L);
    option.setOrder(0);

    PollOption anotherOption = new PollOption();
    anotherOption.setIdPollOption(ANOTHER_ID_POLL_OPTION);
    anotherOption.setVotes(1L);
    anotherOption.setOrder(1);

    poll.setPollOptions(Arrays.asList(option, anotherOption));
    return poll;
  }
}
