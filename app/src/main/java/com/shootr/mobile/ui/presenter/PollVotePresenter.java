package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.Poll;
import com.shootr.mobile.domain.PollStatus;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.poll.GetPollByIdPollInteractor;
import com.shootr.mobile.domain.interactor.poll.GetPollByIdStreamInteractor;
import com.shootr.mobile.domain.interactor.poll.IgnorePollInteractor;
import com.shootr.mobile.domain.interactor.poll.VotePollOptionInteractor;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.mappers.PollModelMapper;
import com.shootr.mobile.ui.views.PollVoteView;
import com.shootr.mobile.util.ErrorMessageFactory;
import javax.inject.Inject;

public class PollVotePresenter implements Presenter {

  private final GetPollByIdStreamInteractor getPollByIdStreamInteractor;
  private final GetPollByIdPollInteractor getPollByIdPollInteractor;
  private final IgnorePollInteractor ignorePollInteractor;
  private final VotePollOptionInteractor votePollOptionInteractor;
  private final PollModelMapper pollModelMapper;
  private final ErrorMessageFactory errorMessageFactory;

  private PollVoteView pollVoteView;
  private String idStream;
  private String idPoll;
  private boolean hasBeenPaused;
  private PollModel pollModel;

  @Inject public PollVotePresenter(GetPollByIdStreamInteractor getPollByIdStreamInteractor,
      GetPollByIdPollInteractor getPollByIdPollInteractor,
      IgnorePollInteractor ignorePollInteractor, VotePollOptionInteractor votePollOptionInteractor,
      PollModelMapper pollModelMapper, ErrorMessageFactory errorMessageFactory) {
    this.getPollByIdStreamInteractor = getPollByIdStreamInteractor;
    this.getPollByIdPollInteractor = getPollByIdPollInteractor;
    this.ignorePollInteractor = ignorePollInteractor;
    this.votePollOptionInteractor = votePollOptionInteractor;
    this.pollModelMapper = pollModelMapper;
    this.errorMessageFactory = errorMessageFactory;
  }

  public void initialize(PollVoteView pollVoteView, String idStream) {
    this.idStream = idStream;
    this.pollVoteView = pollVoteView;
    loadPoll();
  }

  public void initializeWithIdPoll(PollVoteView pollVoteView, String idPoll) {
    this.idPoll = idPoll;
    this.pollVoteView = pollVoteView;
    loadPollByIdPoll();
  }

  private void loadPollByIdPoll() {
    getPollByIdPollInteractor.loadPollByIdPoll(idPoll, new Interactor.Callback<Poll>() {
      @Override public void onLoaded(Poll poll) {
        handlePollModel(poll);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        pollVoteView.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }

  public void loadPoll() {
    getPollByIdStreamInteractor.loadPoll(idStream, new Interactor.Callback<Poll>() {
      @Override public void onLoaded(Poll poll) {
        handlePollModel(poll);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        pollVoteView.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }

  private void handlePollModel(Poll poll) {
    pollModel = pollModelMapper.transform(poll);
    if (canRenderPoll()) {
      pollVoteView.renderPoll(pollModel);
    } else {
      if (pollModel != null) {
        pollVoteView.goToResults(pollModel.getIdPoll());
      }
    }
  }

  private boolean canRenderPoll() {
    return pollModel != null
        && !pollModel.getStatus().equals(PollStatus.CLOSED)
        && !pollModel.getVoteStatus().equals(PollStatus.VOTED);
  }

  public void ignorePoll() {
    ignorePollInteractor.ignorePoll(pollModel.getIdPoll(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        pollVoteView.ignorePoll();
      }
    });
  }

  @Override public void resume() {
    if (hasBeenPaused) {
      loadPoll();
    }
  }

  @Override public void pause() {
    hasBeenPaused = true;
  }

  public void voteOption(String pollOptionId) {
    votePollOptionInteractor.vote(pollModel.getIdPoll(), pollOptionId,
        new Interactor.Callback<Poll>() {
          @Override public void onLoaded(Poll poll) {
            pollVoteView.goToResults(pollModel.getIdPoll());
          }
        });
  }
}
