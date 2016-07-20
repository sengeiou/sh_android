package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.Poll;
import com.shootr.mobile.domain.PollStatus;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.poll.GetPollByIdStreamInteractor;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.mappers.PollModelMapper;
import com.shootr.mobile.ui.model.mappers.PollOptionModelMapper;
import com.shootr.mobile.ui.views.StreamPollView;
import com.shootr.mobile.util.ErrorMessageFactory;
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

public class StreamPollIndicatorPresenterTest {

  private static final String STREAM_ID = "idStream";
  private static final String HOLDER_USER_ID = "idUserHolder";
  private static final String POLL_ID = "idPoll";
  private static final String QUESTION = "question";
  private static final String STATUS = "status";
  private static final boolean PUBLISHED = true;
  private static final boolean NOT_PUBLISHED = false;
  private static final String VOTE = PollStatus.VOTE;
  private static final String VOTED = PollStatus.VOTED;
  private static final String CLOSED = PollStatus.CLOSED;
  private static final String IGNORED = PollStatus.IGNORED;
  private static final String HASSEENRESULTS = "HASSEENRESULTS";

  @Mock StreamPollView streamPollView;
  @Mock GetPollByIdStreamInteractor getPollByIdStreamInteractor;
  @Mock ErrorMessageFactory errorMessageFactory;

  private StreamPollIndicatorPresenter presenter;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    PollModelMapper pollModelMapper = new PollModelMapper(new PollOptionModelMapper());
    presenter = new StreamPollIndicatorPresenter(getPollByIdStreamInteractor, pollModelMapper,
        errorMessageFactory);
  }

  @Test public void shouldShowPollIndicatorWithVoteActionWhenStreamHaveAPublishedPollNotVoted()
      throws Exception {
    setupGetVotePollByIdStreamInteractor();

    presenter.initialize(streamPollView, STREAM_ID, HOLDER_USER_ID);

    verify(streamPollView).showPollIndicatorWithVoteAction(any(PollModel.class));
  }

  @Test public void shouldShowPollIndicatorWithViewActionWhenStreamHaveAPublishedPollVoted()
      throws Exception {
    setupGetVotedPollByIdStreamInteractor();

    presenter.initialize(streamPollView, STREAM_ID, HOLDER_USER_ID);

    verify(streamPollView).showPollIndicatorWithViewAction(any(PollModel.class));
  }

  @Test public void shouldShowPollIndicatorWithViewActionWhenStreamHaveASeenResultsPoll()
      throws Exception {
    setupGetSeenResultsPollByIdStreamInteractor();

    presenter.initialize(streamPollView, STREAM_ID, HOLDER_USER_ID);

    verify(streamPollView).showPollIndicatorWithViewAction(any(PollModel.class));
  }

  @Test public void shouldShowPollIndicatorWithResultsActionWhenStreamHaveAPublishedPollClosed()
      throws Exception {
    setupGetClosedPollByIdStreamInteractor();

    presenter.initialize(streamPollView, STREAM_ID, HOLDER_USER_ID);

    verify(streamPollView).showPollIndicatorWithResultsAction(any(PollModel.class));
  }

  @Test public void shouldNotShowPollIndicatorWhenStreamHaveANotPublishedPoll() throws Exception {
    setupGetNotPublishedPollByIdStreamInteractor();

    presenter.initialize(streamPollView, STREAM_ID, HOLDER_USER_ID);

    verify(streamPollView).hidePollIndicator();
  }

  @Test public void shouldNotShowPollIndicatorWhenStreamPollIsIgnored() throws Exception {
    setupGetIgnoredPollByIdStreamInteractor();

    presenter.initialize(streamPollView, STREAM_ID, HOLDER_USER_ID);

    verify(streamPollView).hidePollIndicator();
  }

  @Test public void shouldNotShowPollIndicatorWhenStreamPollIsNull() throws Exception {
    setupGetNullPollByIdStreamInteractor();

    presenter.initialize(streamPollView, STREAM_ID, HOLDER_USER_ID);

    verify(streamPollView).hidePollIndicator();
  }

  @Test public void shouldShowErrorInViewWhenInteractorReturnsError() throws Exception {
    setupGetPollByIdStreamErrorCallback();

    presenter.initialize(streamPollView, STREAM_ID, HOLDER_USER_ID);

    verify(streamPollView).showError(anyString());
  }

  @Test public void shouldLoadPollWhenResume() throws Exception {
    setupGetVotePollByIdStreamInteractor();
    presenter.initialize(streamPollView, STREAM_ID, HOLDER_USER_ID);

    presenter.resume();

    verify(streamPollView).showPollIndicatorWithVoteAction(any(PollModel.class));
  }

  @Test public void shouldGoToPollResultsInOnActionPressedAndActionIsResults() throws Exception {
    setupGetClosedPollByIdStreamInteractor();
    presenter.initialize(streamPollView, STREAM_ID, HOLDER_USER_ID);

    presenter.onActionPressed();

    verify(streamPollView).goToPollResults(anyString());
  }

  @Test public void shouldGoToPollVoteInOnActionPressedAndActionIsVote() throws Exception {
    setupGetVotePollByIdStreamInteractor();
    presenter.initialize(streamPollView, STREAM_ID, HOLDER_USER_ID);

    presenter.onActionPressed();

    verify(streamPollView).goToPollVote(anyString(), anyString());
  }

  @Test public void shouldGoToPollLiveResultsInOnActionPressedAndActionIsLiveResults()
      throws Exception {
    setupGetVotedPollByIdStreamInteractor();
    presenter.initialize(streamPollView, STREAM_ID, HOLDER_USER_ID);

    presenter.onActionPressed();

    verify(streamPollView).goToPollLiveResults(anyString());
  }

  private void setupGetVotePollByIdStreamInteractor() {
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

  private void setupGetVotedPollByIdStreamInteractor() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<Poll> callback =
            (Interactor.Callback<Poll>) invocation.getArguments()[1];
        callback.onLoaded(votedPoll());
        return null;
      }
    }).when(getPollByIdStreamInteractor)
        .loadPoll(anyString(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
  }

  private void setupGetSeenResultsPollByIdStreamInteractor() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<Poll> callback =
            (Interactor.Callback<Poll>) invocation.getArguments()[1];
        callback.onLoaded(seenResultsPoll());
        return null;
      }
    }).when(getPollByIdStreamInteractor)
        .loadPoll(anyString(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
  }

  private void setupGetClosedPollByIdStreamInteractor() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<Poll> callback =
            (Interactor.Callback<Poll>) invocation.getArguments()[1];
        callback.onLoaded(closedPoll());
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

  private void setupGetNotPublishedPollByIdStreamInteractor() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<Poll> callback =
            (Interactor.Callback<Poll>) invocation.getArguments()[1];
        callback.onLoaded(notPublishedPoll());
        return null;
      }
    }).when(getPollByIdStreamInteractor)
        .loadPoll(anyString(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
  }

  private void setupGetIgnoredPollByIdStreamInteractor() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Poll poll = poll();
        poll.setVoteStatus(IGNORED);
        Interactor.Callback<Poll> callback =
            (Interactor.Callback<Poll>) invocation.getArguments()[1];
        callback.onLoaded(notPublishedPoll());
        return null;
      }
    }).when(getPollByIdStreamInteractor)
        .loadPoll(anyString(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
  }

  private void setupGetNullPollByIdStreamInteractor() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<Poll> callback =
            (Interactor.Callback<Poll>) invocation.getArguments()[1];
        callback.onLoaded(null);
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
    poll.setVoteStatus(VOTE);
    return poll;
  }

  private Poll closedPoll() {
    Poll poll = new Poll();
    poll.setIdStream(STREAM_ID);
    poll.setIdPoll(POLL_ID);
    poll.setQuestion(QUESTION);
    poll.setStatus(CLOSED);
    poll.setPublished(PUBLISHED);
    poll.setVoteStatus(VOTED);
    return poll;
  }

  private Poll votedPoll() {
    Poll poll = new Poll();
    poll.setIdStream(STREAM_ID);
    poll.setIdPoll(POLL_ID);
    poll.setQuestion(QUESTION);
    poll.setStatus(STATUS);
    poll.setPublished(PUBLISHED);
    poll.setVoteStatus(VOTED);
    return poll;
  }

  private Poll seenResultsPoll() {
    Poll poll = new Poll();
    poll.setIdStream(STREAM_ID);
    poll.setIdPoll(POLL_ID);
    poll.setQuestion(QUESTION);
    poll.setStatus(STATUS);
    poll.setPublished(PUBLISHED);
    poll.setVoteStatus(HASSEENRESULTS);
    return poll;
  }

  private Poll notPublishedPoll() {
    Poll poll = new Poll();
    poll.setIdStream(STREAM_ID);
    poll.setIdPoll(POLL_ID);
    poll.setQuestion(QUESTION);
    poll.setStatus(STATUS);
    poll.setPublished(NOT_PUBLISHED);
    poll.setVoteStatus(VOTE);
    return poll;
  }
}
