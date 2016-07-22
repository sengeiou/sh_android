package com.shootr.mobile.domain.interactor.poll;

import com.shootr.mobile.domain.Poll;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.poll.InternalPollRepository;
import com.shootr.mobile.domain.repository.poll.ServerPollRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IgnorePollInteractorTest {

  public static final String ID_POLL = "id_poll";
  public static final String IGNORED = "IGNORED";
  public static final String VOTE = "VOTE";
  private IgnorePollInteractor ignorePollInteractor;
  @Mock InternalPollRepository localPollRepository;
  @Mock ServerPollRepository remotePollRepository;
  @Mock Interactor.CompletedCallback callback;
  @Mock Interactor.ErrorCallback errorCallback;
  @Captor ArgumentCaptor<Poll> pollArgumentCaptor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    ignorePollInteractor =
        new IgnorePollInteractor(interactorHandler, postExecutionThread, localPollRepository);
  }

  @Test public void shouldGetPollFromLocalRepository() throws Exception {
    when(localPollRepository.getPollByIdPoll(ID_POLL)).thenReturn(poll());

    ignorePollInteractor.ignorePoll(ID_POLL, callback);

    verify(localPollRepository).getPollByIdPoll(ID_POLL);
  }

  @Test public void shouldPutIgnoredPollInLocalRepository() throws Exception {
    when(localPollRepository.getPollByIdPoll(ID_POLL)).thenReturn(poll());

    ignorePollInteractor.ignorePoll(ID_POLL, callback);

    verify(localPollRepository).putPoll(pollArgumentCaptor.capture());
    assertTrue(pollArgumentCaptor.getValue().getVoteStatus().equals(IGNORED));
  }

  @Test public void shouldNotifyCompleted() throws Exception {
    when(localPollRepository.getPollByIdPoll(ID_POLL)).thenReturn(poll());

    ignorePollInteractor.ignorePoll(ID_POLL, callback);

    verify(callback).onCompleted();
  }

  private Poll poll() {
    Poll poll = new Poll();
    poll.setIdPoll(ID_POLL);
    poll.setStatus(VOTE);
    return poll;
  }
}
