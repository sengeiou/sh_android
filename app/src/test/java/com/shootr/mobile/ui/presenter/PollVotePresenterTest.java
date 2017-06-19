package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.poll.GetPollByIdPollInteractor;
import com.shootr.mobile.domain.interactor.poll.GetPollByIdStreamInteractor;
import com.shootr.mobile.domain.interactor.poll.IgnorePollInteractor;
import com.shootr.mobile.domain.interactor.poll.ShowPollResultsInteractor;
import com.shootr.mobile.domain.interactor.poll.VotePollOptionInteractor;
import com.shootr.mobile.domain.interactor.stream.GetStreamInteractor;
import com.shootr.mobile.domain.model.poll.Poll;
import com.shootr.mobile.domain.model.poll.PollOption;
import com.shootr.mobile.domain.model.poll.PollStatus;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.user.Contributor;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.Poller;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.mappers.PollModelMapper;
import com.shootr.mobile.ui.model.mappers.PollOptionModelMapper;
import com.shootr.mobile.ui.views.PollVoteView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PollVotePresenterTest {

  private static final String STREAM_ID = "idStream";
  private static final String HOLDER_USER_ID = "idUserHolder";
  private static final String POLL_ID = "idPoll";
  private static final String QUESTION = "question";
  private static final String STATUS = "status";
  private static final String TITLE = "title";
  private static final String POLL_OPTION_ID = "idPollOption";
  private static final String ID_USER = "idUser";
  private static final String ANOTHER_USER_ID = "anotherIdUser";
  private static final long POLL_VOTES = 25L;

  @Mock PollVoteView pollVoteView;
  @Mock GetPollByIdStreamInteractor getPollByIdStreamInteractor;
  @Mock IgnorePollInteractor ignorePollInteractor;
  @Mock VotePollOptionInteractor votePollOptionInteractor;
  @Mock ErrorMessageFactory errorMessageFactory;
  @Mock GetPollByIdPollInteractor getPollByIdPollInteractor;
  @Mock GetStreamInteractor getStreamInteractor;
  @Mock ShowPollResultsInteractor showPollResultsInteractor;
  @Mock SessionRepository sessionRepository;
  @Mock Poller poller;

  private PollVotePresenter presenter;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    when(sessionRepository.getCurrentUserId()).thenReturn(HOLDER_USER_ID);
    PollModelMapper pollModelMapper = new PollModelMapper(new PollOptionModelMapper());
    presenter = new PollVotePresenter(getPollByIdStreamInteractor, getPollByIdPollInteractor,
        ignorePollInteractor, votePollOptionInteractor,
        showPollResultsInteractor, getStreamInteractor, sessionRepository, pollModelMapper, errorMessageFactory,
        poller);
  }

  @Test public void shouldRenderPollModelInView() throws Exception {
    setupGetPollByIdStreamInteractorCallback();

    presenter.initialize(pollVoteView, STREAM_ID, HOLDER_USER_ID);

    verify(pollVoteView).renderPoll(any(PollModel.class));
  }

  @Test public void shouldShowPollVotesWhenPollModelLoaded() throws Exception {
    setupGetPollByIdStreamInteractorCallback();

    presenter.initialize(pollVoteView, STREAM_ID, HOLDER_USER_ID);

    verify(pollVoteView).showPollVotesTimeToExpire(anyLong(), anyLong(), anyBoolean());
  }

  @Test public void shouldShowErrorInViewWhenInteractorReturnsError() throws Exception {

    setupGetPollByIdStreamErrorCallback();

    presenter.initialize(pollVoteView, STREAM_ID, HOLDER_USER_ID);

    verify(pollVoteView).showError(anyString());
  }

  @Test public void shouldIgnorePollInViewWhenIgnorePollCallback() throws Exception {
    setupGetPollByIdStreamInteractorCallback();
    setupIgnorePollInteractorCallback();

    presenter.initialize(pollVoteView, STREAM_ID, HOLDER_USER_ID);
    presenter.ignorePoll();

    verify(pollVoteView).ignorePoll();
  }

  @Test public void shouldGoToPollResultsWhenVoteOption() throws Exception {
    setupGetPollByIdStreamInteractorCallback();
    setupVotePollOptionInteractorCallback();
    presenter.initialize(pollVoteView, STREAM_ID, HOLDER_USER_ID);

    presenter.voteOption(POLL_OPTION_ID);

    verify(pollVoteView).goToResults(anyString(), anyString());
  }

  @Test public void shouldGoToResultsWhenInitializeAndPollIsVoted() throws Exception {
    setupGetVotedPollByIdStreamInteractorCallback();

    presenter.initialize(pollVoteView, STREAM_ID, HOLDER_USER_ID);

    verify(pollVoteView).goToResults(anyString(), anyString());
  }

  @Test public void shouldGoToResultsWhenInitializeAndPollIsClosed() throws Exception {
    setupGetClosedPollByIdStreamInteractorCallback();

    presenter.initialize(pollVoteView, STREAM_ID, HOLDER_USER_ID);

    verify(pollVoteView).goToResults(anyString(), anyString());
  }

  @Test public void shouldGoToResultsWhenInitializeAndHadSeenPollResults() throws Exception {
    setupGetSeenResultsPollByIdStreamInteractorCallback();

    presenter.initialize(pollVoteView, STREAM_ID, HOLDER_USER_ID);

    verify(pollVoteView).goToResults(anyString(), anyString());
  }

  @Test public void shouldLoadByIdPollWhenViewInitializesByIdPoll() throws Exception {
    setupGetPollByIdPollInteractorCallback();

    presenter.initializeWithIdPoll(pollVoteView, POLL_ID);

    verify(pollVoteView).renderPoll(any(PollModel.class));
  }

  @Test public void shouldLoadPollByIdPollInResumeWhenInitializeWithIdPoll() throws Exception {
    setupGetPollByIdPollInteractorCallback();
    presenter.initializeWithIdPoll(pollVoteView, POLL_ID);
    presenter.pause();

    presenter.resume();

    verify(pollVoteView, times(2)).renderPoll(any(PollModel.class));
  }

  @Test public void shouldLoadPollByIdStreeamInResumeWhenInitializeWithIdStream() throws Exception {
    setupGetPollByIdStreamInteractorCallback();
    presenter.initialize(pollVoteView, STREAM_ID, HOLDER_USER_ID);
    presenter.pause();

    presenter.resume();

    verify(pollVoteView, times(2)).renderPoll(any(PollModel.class));
  }

  @Test public void shouldShowErrorAlertWhenVotePollOptionError() throws Exception {
    setupGetPollByIdStreamInteractorCallback();
    setupVotePollOptionInteractorErrorCallback();
    presenter.initialize(pollVoteView, STREAM_ID, HOLDER_USER_ID);

    presenter.voteOption(POLL_OPTION_ID);

    verify(pollVoteView).showTimeoutAlert();
  }

  @Test public void shouldGoToPollResultsWhenVRetryVote() throws Exception {
    setupGetPollByIdStreamInteractorCallback();
    setupVotePollOptionInteractorCallback();
    presenter.initialize(pollVoteView, STREAM_ID, HOLDER_USER_ID);
    presenter.setVotedPollOption(POLL_OPTION_ID);

    presenter.retryVote();

    verify(pollVoteView).goToResults(anyString(), anyString());
  }

  @Test public void shouldShowErrorAlertWhenRetryVoteError() throws Exception {
    setupGetPollByIdStreamInteractorCallback();
    setupVotePollOptionInteractorErrorCallback();
    presenter.initialize(pollVoteView, STREAM_ID, HOLDER_USER_ID);
    presenter.setVotedPollOption(POLL_OPTION_ID);

    presenter.voteOption(POLL_OPTION_ID);

    verify(pollVoteView).showTimeoutAlert();
  }

  @Test public void shouldShowPollResultsDialogWhenUserIsNotContributor() throws Exception {
    setupGetPollByIdPollInteractorCallback();
    setupGetStreamInteractorCallback(1L, false);
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
    presenter.initializeWithIdPoll(pollVoteView, POLL_ID);

    presenter.onShowPollResults();

    verify(pollVoteView).showResultsWithoutVotingDialog();
  }

  @Test public void shouldNotShowPollResultsDialogWhenUserIsContributor() throws Exception {
    setupGetPollByIdPollInteractorCallback();
    presenter.initializeWithIdPoll(pollVoteView, POLL_ID);

    presenter.onShowPollResults();

    verify(pollVoteView, never()).showResultsWithoutVotingDialog();
  }

  @Test public void shouldShowPollResultsDialogWhenUserIsNotPollOwner() throws Exception {
    setupGetPollByIdPollInteractorCallback();
    setupGetStreamInteractorCallback(0L, false);
    when(sessionRepository.getCurrentUserId()).thenReturn(ANOTHER_USER_ID);
    presenter.initializeWithIdPoll(pollVoteView, POLL_ID);

    presenter.onShowPollResults();

    verify(pollVoteView).showResultsWithoutVotingDialog();
  }

  @Test public void shouldNotShowPollResultsDialogWhenUserIsPollOwner() throws Exception {
    setupGetPollByIdPollInteractorCallback();
    presenter.initializeWithIdPoll(pollVoteView, POLL_ID);

    presenter.onShowPollResults();

    verify(pollVoteView, never()).showResultsWithoutVotingDialog();
  }

  @Test public void shouldGoToResultsWhenShowPollResultsWithoutVoting() throws Exception {
    setupGetPollByIdPollInteractorCallback();
    setupShowPollResultsInteractorCallback();
    presenter.initializeWithIdPoll(pollVoteView, POLL_ID);

    presenter.showPollResultsWithoutVoting();

    verify(pollVoteView).goToResults(anyString(), anyString());
  }

  private void setupShowPollResultsInteractorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.CompletedCallback callback =
            (Interactor.CompletedCallback) invocation.getArguments()[1];
        callback.onCompleted();
        return null;
      }
    }).when(showPollResultsInteractor)
        .showPollResults(anyString(), any(Interactor.CompletedCallback.class));
  }

  private void setupIgnorePollInteractorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.CompletedCallback callback =
            (Interactor.CompletedCallback) invocation.getArguments()[1];
        callback.onCompleted();
        return null;
      }
    }).when(ignorePollInteractor).ignorePoll(anyString(), any(Interactor.CompletedCallback.class));
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

  private void setupGetVotedPollByIdStreamInteractorCallback() {
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

  private void setupGetClosedPollByIdStreamInteractorCallback() {
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

  private void setupGetSeenResultsPollByIdStreamInteractorCallback() {
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

  private void setupGetPollByIdPollInteractorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<Poll> callback =
            (Interactor.Callback<Poll>) invocation.getArguments()[1];
        callback.onLoaded(poll());
        return null;
      }
    }).when(getPollByIdPollInteractor)
        .loadPollByIdPoll(anyString(), any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
  }

  private void setupVotePollOptionInteractorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback callback = (Interactor.Callback) invocation.getArguments()[3];
        callback.onLoaded(poll());
        return null;
      }
    }).when(votePollOptionInteractor)
        .vote(anyString(), anyString(), anyBoolean(), any(Interactor.Callback.class), any(
            Interactor.ErrorCallback.class));
  }

  private void setupVotePollOptionInteractorErrorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.ErrorCallback errorCallback = (Interactor.ErrorCallback) invocation.getArguments()[4];
        errorCallback.onError(new ShootrException() { });
        return null;
      }
    }).when(votePollOptionInteractor)
        .vote(anyString(), anyString(), anyBoolean(), any(Interactor.Callback.class), any(
            Interactor.ErrorCallback.class));
  }

  private void setupGetStreamInteractorCallback(final long contributorsCount, final boolean isContributor) {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        GetStreamInteractor.Callback callback =
            (GetStreamInteractor.Callback) invocation.getArguments()[1];
        callback.onLoaded(stream(contributorsCount, isContributor));
        return null;
      }
    }).when(getStreamInteractor)
        .loadStream(anyString(),
            any(GetStreamInteractor.Callback.class));
  }

  private List<Contributor> contributors() {
    List<Contributor> contributors = new ArrayList<>();
    contributors.add(contributor());
    return contributors;
  }

  private Contributor contributor() {
    Contributor contributor = new Contributor();
    contributor.setIdUser(HOLDER_USER_ID);
    return contributor;
  }

  private Poll votedPoll() {
    Poll poll = new Poll();
    poll.setIdStream(STREAM_ID);
    poll.setIdPoll(POLL_ID);
    poll.setQuestion(QUESTION);
    poll.setVoteStatus(PollStatus.VOTED);
    poll.setStatus(PollStatus.OPEN);
    return poll;
  }

  private Poll closedPoll() {
    Poll poll = new Poll();
    poll.setIdStream(STREAM_ID);
    poll.setIdPoll(POLL_ID);
    poll.setQuestion(QUESTION);
    poll.setVoteStatus(PollStatus.VOTE);
    poll.setStatus(PollStatus.CLOSED);
    return poll;
  }

  private Poll seenResultsPoll() {
    Poll poll = new Poll();
    poll.setIdStream(STREAM_ID);
    poll.setIdPoll(POLL_ID);
    poll.setQuestion(QUESTION);
    poll.setVoteStatus(PollStatus.HASSEENRESULTS);
    poll.setStatus(PollStatus.CLOSED);
    return poll;
  }

  private Poll poll() {
    Poll poll = new Poll();
    poll.setIdStream(STREAM_ID);
    poll.setIdPoll(POLL_ID);
    poll.setIdUser(HOLDER_USER_ID);
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
    pollOption.setVotes(POLL_VOTES);
    return Collections.singletonList(pollOption);
  }

  private Stream stream(long contributorsCount, boolean isContributor) {
    Stream stream = new Stream();
    stream.setId(STREAM_ID);
    stream.setTitle(TITLE);
    stream.setContributorCount(contributorsCount);
    stream.setCurrentUserContributor(isContributor);
    return stream;
  }
}
