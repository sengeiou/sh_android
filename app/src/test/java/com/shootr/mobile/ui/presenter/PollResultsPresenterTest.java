package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.model.poll.Poll;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.poll.GetPollByIdPollInteractor;
import com.shootr.mobile.domain.interactor.poll.IgnorePollInteractor;
import com.shootr.mobile.domain.interactor.poll.SharePollInteractor;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.mappers.PollModelMapper;
import com.shootr.mobile.ui.model.mappers.PollOptionModelMapper;
import com.shootr.mobile.ui.views.PollResultsView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class PollResultsPresenterTest {

  private static final String POLL_ID = "idPoll";
  private static final String QUESTION = "question";
  private static final String STATUS = "status";
  private static final boolean PUBLISHED = true;
  private static final String STREAM_ID = "idStream";

  @Mock PollResultsView pollResultsView;
  @Mock GetPollByIdPollInteractor getPollByIdPollInteractor;
  @Mock IgnorePollInteractor ignorePollInteractor;
  @Mock SharePollInteractor sharePollInteractor;
  @Mock ErrorMessageFactory errorMessageFactory;

  private PollResultsPresenter presenter;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    PollModelMapper pollModelMapper = new PollModelMapper(new PollOptionModelMapper());
    presenter = new PollResultsPresenter(getPollByIdPollInteractor, pollModelMapper,
        ignorePollInteractor, sharePollInteractor, errorMessageFactory);
  }

  @Test public void shouldRenderResultsWhenInitialize() throws Exception {
    setupGetPollByIdStreamInteractor();

    presenter.initialize(pollResultsView, POLL_ID);

    verify(pollResultsView).renderPollResults(any(PollModel.class));
  }

  @Test public void shouldShowPollVotesWhenInitialize() throws Exception {
    setupGetPollByIdStreamInteractor();

    presenter.initialize(pollResultsView, POLL_ID);

    verify(pollResultsView).showPollVotes(anyLong());
  }

  @Test public void shouldShowErrorInViewWhenInteractorReturnsError() throws Exception {
    setupGetPollByIdPollErrorCallback();

    presenter.initialize(pollResultsView, POLL_ID);

    verify(pollResultsView).showError(anyString());
  }

  @Test public void shouldLoadPollWhenResume() throws Exception {
    setupGetPollByIdStreamInteractor();
    presenter.initialize(pollResultsView, POLL_ID);

    presenter.resume();

    verify(pollResultsView).renderPollResults(any(PollModel.class));
  }

  @Test
  public void shouldIgnorePollInViewWhenIgnorePollCallback() throws Exception {
    setupGetPollByIdStreamInteractor();
    setupIgnorePollInteractorCallback();

    presenter.initialize(pollResultsView, POLL_ID);
    presenter.ignorePoll();

    verify(pollResultsView).ignorePoll();
  }

  @Test public void shouldShareInViewWhenSharePressed() throws Exception {
    presenter.setView(pollResultsView);
    presenter.share();

    verify(pollResultsView).share(any(PollModel.class));
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

  private void setupGetPollByIdStreamInteractor() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<Poll> callback =
            (Interactor.Callback<Poll>) invocation.getArguments()[1];
        callback.onLoaded(poll());
        return null;
      }
    }).when(getPollByIdPollInteractor)
        .loadPollByIdPoll(anyString(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
  }

  private void setupGetPollByIdPollErrorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.ErrorCallback errorCallback =
            (Interactor.ErrorCallback) invocation.getArguments()[2];
        errorCallback.onError(new ShootrException() {
        });
        return null;
      }
    }).when(getPollByIdPollInteractor)
        .loadPollByIdPoll(anyString(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
  }

  private Poll poll() {
    Poll poll = new Poll();
    poll.setIdStream(STREAM_ID);
    poll.setIdPoll(POLL_ID);
    poll.setQuestion(QUESTION);
    poll.setStatus(STATUS);
    poll.setPublished(PUBLISHED);
    poll.setPollOptions(Collections.EMPTY_LIST);
    return poll;
  }

}
