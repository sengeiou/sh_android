package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.Poll;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.poll.GetPollByIdStreamInteractor;
import com.shootr.mobile.domain.interactor.poll.IgnorePollInteractor;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.mappers.PollModelMapper;
import com.shootr.mobile.ui.model.mappers.PollOptionModelMapper;
import com.shootr.mobile.ui.views.PollResultsView;
import java.util.Collections;
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

public class PollResultsPresenterTest {

  private static final String STREAM_ID = "idStream";
  private static final String POLL_ID = "idPoll";
  private static final String QUESTION = "question";
  private static final String STATUS = "status";
  private static final boolean PUBLISHED = true;

  @Mock PollResultsView pollResultsView;
  @Mock GetPollByIdStreamInteractor getPollByIdStreamInteractor;
  @Mock IgnorePollInteractor ignorePollInteractor;

  private PollResultsPresenter presenter;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    PollModelMapper pollModelMapper = new PollModelMapper(new PollOptionModelMapper());
    presenter = new PollResultsPresenter(getPollByIdStreamInteractor, pollModelMapper,
        ignorePollInteractor);
  }

  @Test public void shouldRenderResultsWhenInitialize() throws Exception {
    setupGetPollByIdStreamInteractor();

    presenter.initialize(pollResultsView, STREAM_ID);

    verify(pollResultsView).renderPollResults(any(PollModel.class));
  }

  @Test public void shouldShowErrorInViewWhenInteractorReturnsError() throws Exception {
    setupGetPollByIdStreamErrorCallback();

    presenter.initialize(pollResultsView, STREAM_ID);

    verify(pollResultsView).showError(anyString());
  }

  @Test public void shouldLoadPollWhenResume() throws Exception {
    setupGetPollByIdStreamInteractor();
    presenter.initialize(pollResultsView, STREAM_ID);

    presenter.resume();

    verify(pollResultsView).renderPollResults(any(PollModel.class));
  }

  @Test
  public void shouldIgnorePollInViewWhenIgnorePollCallback() throws Exception {
    setupGetPollByIdStreamInteractor();
    setupIgnorePollInteractorCallback();

    presenter.initialize(pollResultsView, STREAM_ID);
    presenter.ignorePoll();

    verify(pollResultsView).ignorePoll();
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
    }).when(getPollByIdStreamInteractor)
        .loadPoll(anyString(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
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
