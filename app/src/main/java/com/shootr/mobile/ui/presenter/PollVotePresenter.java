package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.poll.GetPollByIdPollInteractor;
import com.shootr.mobile.domain.interactor.poll.GetPollByIdStreamInteractor;
import com.shootr.mobile.domain.interactor.poll.IgnorePollInteractor;
import com.shootr.mobile.domain.interactor.poll.ShowPollResultsInteractor;
import com.shootr.mobile.domain.interactor.poll.VotePollOptionInteractor;
import com.shootr.mobile.domain.interactor.user.contributor.GetContributorsInteractor;
import com.shootr.mobile.domain.model.poll.Poll;
import com.shootr.mobile.domain.model.poll.PollStatus;
import com.shootr.mobile.domain.model.user.Contributor;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.ui.model.mappers.PollModelMapper;
import com.shootr.mobile.ui.views.PollVoteView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class PollVotePresenter implements Presenter {

  private static final long ZERO_VOTES = 0;

  private final GetPollByIdStreamInteractor getPollByIdStreamInteractor;
  private final GetPollByIdPollInteractor getPollByIdPollInteractor;
  private final IgnorePollInteractor ignorePollInteractor;
  private final VotePollOptionInteractor votePollOptionInteractor;
  private final GetContributorsInteractor getContributorsInteractor;
  private final ShowPollResultsInteractor showPollResultsInteractor;
  private final SessionRepository sessionRepository;
  private final PollModelMapper pollModelMapper;
  private final ErrorMessageFactory errorMessageFactory;

  private PollVoteView pollVoteView;
  private String idStream;
  private String idStreamOwner;
  private String idPoll;
  private boolean hasBeenPaused;
  private boolean hasBeenInitializedWithIdPoll;
  private PollModel pollModel;
  private String votedPollOptionId;
  private long pollVotes = 0;

  @Inject public PollVotePresenter(GetPollByIdStreamInteractor getPollByIdStreamInteractor,
      GetPollByIdPollInteractor getPollByIdPollInteractor,
      IgnorePollInteractor ignorePollInteractor, VotePollOptionInteractor votePollOptionInteractor,
      GetContributorsInteractor getContributorsInteractor,
      ShowPollResultsInteractor showPollResultsInteractor, SessionRepository sessionRepository,
      PollModelMapper pollModelMapper, ErrorMessageFactory errorMessageFactory) {
    this.getPollByIdStreamInteractor = getPollByIdStreamInteractor;
    this.getPollByIdPollInteractor = getPollByIdPollInteractor;
    this.ignorePollInteractor = ignorePollInteractor;
    this.votePollOptionInteractor = votePollOptionInteractor;
    this.getContributorsInteractor = getContributorsInteractor;
    this.showPollResultsInteractor = showPollResultsInteractor;
    this.sessionRepository = sessionRepository;
    this.pollModelMapper = pollModelMapper;
    this.errorMessageFactory = errorMessageFactory;
  }

  public void initialize(PollVoteView pollVoteView, String idStream, String idStreamOwner) {
    this.idStream = idStream;
    this.idStreamOwner = idStreamOwner;
    this.pollVoteView = pollVoteView;
    this.hasBeenInitializedWithIdPoll = false;
    loadPollByIdStream();
    if (sessionRepository.getCurrentUserId().equals(idStreamOwner)) {
      pollVoteView.showViewResultsButton();
    }
  }

  public void initializeWithIdPoll(PollVoteView pollVoteView, String idPoll) {
    this.idPoll = idPoll;
    this.pollVoteView = pollVoteView;
    this.hasBeenInitializedWithIdPoll = true;
    loadPollByIdPoll();
  }

  private void loadPollByIdPoll() {
    pollVoteView.showLoading();
    getPollByIdPollInteractor.loadPollByIdPoll(idPoll, new Interactor.Callback<Poll>() {
      @Override public void onLoaded(Poll poll) {
        handlePollModel(poll);
        pollVoteView.hideLoading();
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        pollVoteView.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }

  public void loadPollByIdStream() {
    pollVoteView.showLoading();
    getPollByIdStreamInteractor.loadPoll(idStream, new Interactor.Callback<Poll>() {
      @Override public void onLoaded(Poll poll) {
        handlePollModel(poll);
        pollVoteView.hideLoading();
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
      idStream = pollModel.getIdStream();
      pollVoteView.renderPoll(pollModel);
      showPollVotes();
    } else {
      if (pollModel != null) {
        pollVoteView.goToResults(pollModel.getIdPoll(), pollModel.getIdStream());
      }
    }
  }

  private void showPollVotes() {
    countPollVotes();
    pollVoteView.showPollVotes(pollVotes);
  }

  private void countPollVotes() {
    pollVotes = ZERO_VOTES;
    for (PollOptionModel pollOptionModel : pollModel.getPollOptionModels()) {
      pollVotes += pollOptionModel.getVotes();
    }
  }

  private boolean canRenderPoll() {
    return pollModel != null
        && !pollModel.getStatus().equals(PollStatus.CLOSED)
        && !pollModel.getVoteStatus().equals(PollStatus.VOTED)
        && !pollModel.getVoteStatus().equals(PollStatus.HASSEENRESULTS);
  }

  public void ignorePoll() {
    ignorePollInteractor.ignorePoll(pollModel.getIdPoll(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        pollVoteView.ignorePoll();
      }
    });
  }

  private void loadPollInResume() {
    if (hasBeenInitializedWithIdPoll) {
      loadPollByIdPoll();
    } else {
      loadPollByIdStream();
    }
  }

  public void voteOption(String pollOptionId) {
    pollVoteView.showLoading();
    setVotedPollOption(pollOptionId);
    if (pollModel != null) {
      votePollOptionInteractor.vote(pollModel.getIdPoll(), pollOptionId, new Interactor.Callback<Poll>() {
        @Override public void onLoaded(Poll poll) {
          pollVoteView.hideLoading();
          pollVoteView.goToResults(pollModel.getIdPoll(), pollModel.getIdStream());
        }
      }, new Interactor.ErrorCallback() {
        @Override public void onError(ShootrException error) {
          pollVoteView.hideLoading();
          pollVoteView.showTimeoutAlert();
        }
      });
    }
  }

  public void retryVote() {
    pollVoteView.showLoading();
    votePollOptionInteractor.vote(pollModel.getIdPoll(), votedPollOptionId,
        new Interactor.Callback<Poll>() {
          @Override public void onLoaded(Poll poll) {
            pollVoteView.hideLoading();
            pollVoteView.goToResults(pollModel.getIdPoll(), pollModel.getIdStream());
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            pollVoteView.hideLoading();
            pollVoteView.showTimeoutAlert();
          }
        });
  }

  protected void setVotedPollOption(String pollOptionId) {
    this.votedPollOptionId = pollOptionId;
  }

  @Override public void resume() {
    if (hasBeenPaused) {
      pollVotes = ZERO_VOTES;
      loadPollInResume();
    }
  }

  @Override public void pause() {
    hasBeenPaused = true;
  }

  public void viewResults() {
    pollVoteView.goToResults(pollModel.getIdPoll(), pollModel.getIdStream());
  }

  public void onShowPollResults() {
    getContributorsInteractor.obtainContributors(idStream, false,
        new Interactor.Callback<List<Contributor>>() {
          @Override public void onLoaded(List<Contributor> contributors) {
            if (!isPollOwner() && !isContributor(contributors)) {
              pollVoteView.showResultsWithoutVotingDialog();
            } else {
              pollVoteView.goToResults(pollModel.getIdPoll(), pollModel.getIdStream());
            }
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            pollVoteView.showError(errorMessageFactory.getMessageForError(error));
          }
        });
  }

  private boolean isPollOwner() {
    return pollModel.getIdUser().equals(sessionRepository.getCurrentUserId());
  }

  private boolean isContributor(List<Contributor> contributors) {
    for (Contributor contributor : contributors) {
      if (contributor.getIdUser().equals(sessionRepository.getCurrentUserId())) {
        return true;
      }
    }
    return false;
  }

  public void showPollResultsWithoutVoting() {
    showPollResultsInteractor.showPollResults(pollModel.getIdPoll(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            pollVoteView.goToResults(pollModel.getIdPoll(), pollModel.getIdStream());
          }
        });
  }

  public void onStreamTitleClick() {
    if (idStream != null) {
      pollVoteView.goToStreamTimeline(idStream);
    }
  }
}
