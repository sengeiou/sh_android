package com.shootr.mobile.domain.interactor.poll;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.poll.Poll;
import com.shootr.mobile.domain.repository.poll.InternalPollRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShowPollResultsInteractorTest {

  private static final String ID_POLL = "id_poll";
  private static final String HASSEENRESULTS = "HASSEENRESULTS";
  private static final String VOTE = "VOTE";
  private ShowPollResultsInteractor showPollResultsInteractor;
  @Mock InternalPollRepository localPollRepository;
  @Mock Interactor.CompletedCallback callback;
  @Mock Interactor.ErrorCallback errorCallback;
  @Captor ArgumentCaptor<Poll> pollArgumentCaptor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    showPollResultsInteractor =
        new ShowPollResultsInteractor(interactorHandler, postExecutionThread, localPollRepository);
  }

  @Test public void shouldGetPollFromLocalRepository() throws Exception {
    when(localPollRepository.getPollByIdPoll(ID_POLL)).thenReturn(poll());

    showPollResultsInteractor.showPollResults(ID_POLL, callback);

    verify(localPollRepository).getPollByIdPoll(ID_POLL);
  }

  @Test public void shouldPutSeenPollInLocalRepository() throws Exception {
    when(localPollRepository.getPollByIdPoll(ID_POLL)).thenReturn(poll());

    showPollResultsInteractor.showPollResults(ID_POLL, callback);

    verify(localPollRepository).putPoll(pollArgumentCaptor.capture());
    assertTrue(pollArgumentCaptor.getValue().getVoteStatus().equals(HASSEENRESULTS));
  }

  @Test public void shouldNotifyCompleted() throws Exception {
    when(localPollRepository.getPollByIdPoll(ID_POLL)).thenReturn(poll());

    showPollResultsInteractor.showPollResults(ID_POLL, callback);

    verify(callback).onCompleted();
  }

  private Poll poll() {
    Poll poll = new Poll();
    poll.setIdPoll(ID_POLL);
    poll.setStatus(VOTE);
    return poll;
  }
}
