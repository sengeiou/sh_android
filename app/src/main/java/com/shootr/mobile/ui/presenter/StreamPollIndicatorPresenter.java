package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.Poll;
import com.shootr.mobile.domain.PollStatus;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.poll.GetPollByIdStreamInteractor;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.mappers.PollModelMapper;
import com.shootr.mobile.ui.views.StreamPollView;
import com.shootr.mobile.util.ErrorMessageFactory;
import javax.inject.Inject;

public class StreamPollIndicatorPresenter implements Presenter {

  private static final String RESULTS = "RESULTS";
  private static final String VOTE = "VOTE";
  private static final String VIEW = "VIEW";
  private final GetPollByIdStreamInteractor getPollByIdStreamInteractor;
  private final PollModelMapper pollModelMapper;
  private final ErrorMessageFactory errorMessageFactory;

  private StreamPollView streamPollView;
  private String idStream;
  private String streamAuthorIdUser;
  private boolean hasBeenPaused;
  private String action;
  String idPoll;

  @Inject
  public StreamPollIndicatorPresenter(GetPollByIdStreamInteractor getPollByIdStreamInteractor,
      PollModelMapper pollModelMapper, ErrorMessageFactory errorMessageFactory) {
    this.getPollByIdStreamInteractor = getPollByIdStreamInteractor;
    this.pollModelMapper = pollModelMapper;
    this.errorMessageFactory = errorMessageFactory;
  }

  public void initialize(StreamPollView streamPollView, String idStream,
      String streamAuthorIdUser) {
    this.idStream = idStream;
    this.streamAuthorIdUser = streamAuthorIdUser;
    this.streamPollView = streamPollView;
    loadPoll();
  }

  public void setView(StreamPollView streamPollView) {
    this.streamPollView = streamPollView;
  }

  public void loadPoll() {
    getPollByIdStreamInteractor.loadPoll(idStream, new Interactor.Callback<Poll>() {
      @Override public void onLoaded(Poll poll) {
        handlePollResult(pollModelMapper.transform(poll));
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        streamPollView.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }

  private void handlePollResult(PollModel pollModel) {
    if (canShowIndicator(pollModel)) {
      idPoll = pollModel.getIdPoll();
      getPollAction(pollModel);
    } else {
      streamPollView.hidePollIndicator();
    }
  }

  private void getPollAction(PollModel pollModel) {
    if (pollModel.getStatus().equals(PollStatus.CLOSED)) {
      action = RESULTS;
      streamPollView.showPollIndicatorWithResultsAction(pollModel);
      return;
    }
    handleVoteStatus(pollModel);
  }

  private void handleVoteStatus(PollModel pollModel) {
    if (pollModel.getVoteStatus().equals(PollStatus.VOTED) ||
        pollModel.getVoteStatus().equals(PollStatus.HASSEENRESULTS)) {
      action = VIEW;
      streamPollView.showPollIndicatorWithViewAction(pollModel);
    } else {
      action = VOTE;
      streamPollView.showPollIndicatorWithVoteAction(pollModel);
    }
  }

  private boolean canShowIndicator(PollModel pollModel) {
    return pollModel != null
        && pollModel.getQuestion() != null
        && pollModel.getPublished()
        && !pollModel.getVoteStatus().equals(PollStatus.IGNORED);
  }

  @Override public void resume() {
    if (hasBeenPaused) {
      loadPoll();
    }
  }

  @Override public void pause() {
    hasBeenPaused = true;
  }

  public void onActionPressed() {
    switch (action) {
      case RESULTS:
        streamPollView.goToPollResults(idPoll);
        break;
      case VOTE:
        streamPollView.goToPollVote(idStream, streamAuthorIdUser);
        break;
      default:
        streamPollView.goToPollLiveResults(idPoll);
        break;
    }
  }
}
