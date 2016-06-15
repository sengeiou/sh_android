package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.Poll;
import com.shootr.mobile.domain.PollOption;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.poll.GetPollByIdStreamInteractor;
import com.shootr.mobile.domain.interactor.poll.IgnorePollInteractor;
import com.shootr.mobile.domain.interactor.poll.VotePollOptionInteractor;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.mappers.PollModelMapper;
import com.shootr.mobile.ui.model.mappers.PollOptionModelMapper;
import com.shootr.mobile.ui.views.PollVoteView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class PollVotePresenterTest {

  private static final String STREAM_ID = "idStream";
  private static final String POLL_ID = "idPoll";
  private static final String QUESTION = "question";
  private static final String STATUS = "status";
  private static final String TITLE = "title";
  private static final String POLL_OPTION_ID = "idPollOption";

  @Mock PollVoteView pollVoteView;
  @Mock GetPollByIdStreamInteractor getPollByIdStreamInteractor;
  @Mock IgnorePollInteractor ignorePollInteractor;
  @Mock VotePollOptionInteractor votePollOptionInteractor;
  @Mock ErrorMessageFactory errorMessageFactory;
  private PollVotePresenter presenter;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    PollModelMapper pollModelMapper = new PollModelMapper(new PollOptionModelMapper());
    presenter =
        new PollVotePresenter(getPollByIdStreamInteractor, ignorePollInteractor,
            votePollOptionInteractor, pollModelMapper, errorMessageFactory);
  }

  @Test public void shouldRenderPollModelInView() throws Exception {
    setupGetPollByIdStreamInteractorCallback();

    presenter.initialize(pollVoteView, STREAM_ID);

    verify(pollVoteView).renderPoll(any(PollModel.class));
  }

  @Test public void shouldShowErrorInViewWhenInteractorReturnsError() throws Exception {
    setupGetPollByIdStreamErrorCallback();

    presenter.initialize(pollVoteView, STREAM_ID);

    verify(pollVoteView).showError(anyString());
  }

  @Test
  public void shouldIgnorePollInViewWhenIgnorePollCallback() throws Exception {
    setupGetPollByIdStreamInteractorCallback();
    setupIgnorePollInteractorCallback();

    presenter.initialize(pollVoteView, STREAM_ID);
    presenter.ignorePoll();

    verify(pollVoteView).ignorePoll();
  }

  @Test public void shouldGoToPollResultsWhenVoteOption() throws Exception {
    setupGetPollByIdStreamInteractorCallback();
    setupVotePollOptionInteractorCallback();
    presenter.initialize(pollVoteView, STREAM_ID);

    presenter.voteOption(POLL_OPTION_ID);

    verify(pollVoteView).goToResults(anyString());
  }

  private void setupIgnorePollInteractorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.CompletedCallback callback =
            (Interactor.CompletedCallback) invocation.getArguments()[1];
        callback.onCompleted();
        return null;
      }
    }).when(ignorePollInteractor)
        .ignorePoll(anyString(), any(Interactor.CompletedCallback.class));
  }

  private void setupGetPollByIdStreamErrorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.ErrorCallback errorCallback =
            (Interactor.ErrorCallback) invocation.getArguments()[2];
        errorCallback.onError(new ShootrException() {
        });
        return null;
      }
    }).when(getPollByIdStreamInteractor)
        .loadPoll(anyString(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
  }

  private void setupGetPollByIdStreamInteractorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<Poll> callback =
            (Interactor.Callback<Poll>) invocation.getArguments()[1];
        callback.onLoaded(poll());
        return null;
      }
    }).when(getPollByIdStreamInteractor)
        .loadPoll(anyString(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
  }

  private void setupVotePollOptionInteractorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback callback = (Interactor.Callback) invocation.getArguments()[2];
        callback.onLoaded(poll());
        return null;
      }
    }).when(votePollOptionInteractor).vote(anyString(), anyString(), any(Interactor.Callback.class));
  }

  private Poll poll() {
    Poll poll = new Poll();
    poll.setIdStream(STREAM_ID);
    poll.setIdPoll(POLL_ID);
    poll.setQuestion(QUESTION);
    poll.setStatus(STATUS);
    poll.setPollOptions(pollOptions());
    return poll;
  }

  private List<PollOption> pollOptions() {
    PollOption pollOption = new PollOption();
    pollOption.setTitle(TITLE);
    pollOption.setIdPoll(POLL_ID);
    pollOption.setIdPollOption(POLL_OPTION_ID);
    return Collections.singletonList(pollOption);
  }
}
