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
import com.shootr.mobile.domain.model.poll.PollStatus;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.user.Contributor;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.Poller;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.ui.model.mappers.PollModelMapper;
import com.shootr.mobile.ui.views.PollVoteView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class PollVotePresenter implements Presenter {

  private static final long ZERO_VOTES = 0;
  private static final long REFRESH_INTERVAL_MILLISECONDS_SEC = 1 * 1000;
  private static final long REFRESH_INTERVAL_MILLISECONDS_MIN = 60 * 1000;

  private final GetPollByIdStreamInteractor getPollByIdStreamInteractor;
  private final GetPollByIdPollInteractor getPollByIdPollInteractor;
  private final IgnorePollInteractor ignorePollInteractor;
  private final VotePollOptionInteractor votePollOptionInteractor;
  private final ShowPollResultsInteractor showPollResultsInteractor;
  private final GetStreamInteractor getStreamInteractor;
  private final SessionRepository sessionRepository;
  private final PollModelMapper pollModelMapper;
  private final ErrorMessageFactory errorMessageFactory;
  private final Poller poller;

  private PollVoteView pollVoteView;
  private String idStream;
  private String idPoll;
  private boolean hasBeenPaused;
  private boolean hasBeenInitializedWithIdPoll;
  private PollModel pollModel;
  private String votedPollOptionId;
  private long pollVotes = 0;
  private boolean isPrivateVote = true;

  @Inject public PollVotePresenter(GetPollByIdStreamInteractor getPollByIdStreamInteractor,
      GetPollByIdPollInteractor getPollByIdPollInteractor,
      IgnorePollInteractor ignorePollInteractor, VotePollOptionInteractor votePollOptionInteractor,
      ShowPollResultsInteractor showPollResultsInteractor, GetStreamInteractor getStreamInteractor,
      SessionRepository sessionRepository, PollModelMapper pollModelMapper,
      ErrorMessageFactory errorMessageFactory, Poller poller) {
    this.getPollByIdStreamInteractor = getPollByIdStreamInteractor;
    this.getPollByIdPollInteractor = getPollByIdPollInteractor;
    this.ignorePollInteractor = ignorePollInteractor;
    this.votePollOptionInteractor = votePollOptionInteractor;
    this.showPollResultsInteractor = showPollResultsInteractor;
    this.getStreamInteractor = getStreamInteractor;
    this.sessionRepository = sessionRepository;
    this.pollModelMapper = pollModelMapper;
    this.errorMessageFactory = errorMessageFactory;
    this.poller = poller;
  }

  public void initialize(PollVoteView pollVoteView, String idStream, String idStreamOwner) {
    this.idStream = idStream;
    this.pollVoteView = pollVoteView;
    this.hasBeenInitializedWithIdPoll = false;
    loadPollByIdStream();
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
      showPollVotesTimeToExpire(pollModel.getExpirationDate());
      setupPoller();
      handlePollPrivacy();
    } else {
      if (pollModel != null) {
        pollVoteView.goToResults(pollModel.getIdPoll(), pollModel.getIdStream());
      }
    }
  }

  private void handlePollPrivacy() {
    if (pollModel.getVotePrivacy().equals(PollStatus.PRIVATE)) {
      isPrivateVote = true;
      pollVoteView.showPrivateVotePrivacyDisabled();
    } else {
      isPrivateVote = false;
      pollVoteView.showPublicVotePrivacy();
    }
  }

  private void showPollVotesTimeToExpire(Long expirationDate) {
    countPollVotes();
    pollVoteView.showPollVotesTimeToExpire(pollVotes, expirationDate);
  }

  private void countPollVotes() {
    pollVotes = ZERO_VOTES;
    for (PollOptionModel pollOptionModel : pollModel.getPollOptionModels()) {
      pollVotes += pollOptionModel.getVotes();
    }
  }

  private void setupPoller() {
    this.poller.init(REFRESH_INTERVAL_MILLISECONDS_SEC, new Runnable() {
      @Override public void run() {
        showPollVotesTimeToExpire(pollModel.getExpirationDate());
        if (pollModel.isExpired()) {
          poller.stopPolling();
          pollVoteView.goToResults(pollModel.getIdPoll(), pollModel.getIdStream());
        }
        if (!pollModel.isLessThanHourToExpire()
            && poller.getIntervalMilliseconds() != REFRESH_INTERVAL_MILLISECONDS_MIN) {
          poller.stopPolling();
          poller.setIntervalMilliseconds(REFRESH_INTERVAL_MILLISECONDS_MIN);
          poller.startPolling();
        }
      }
    });
    poller.startPolling();
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
      votePollOptionInteractor.vote(pollModel.getIdPoll(), pollOptionId, isPrivateVote,
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
  }

  public void retryVote() {
    pollVoteView.showLoading();
    votePollOptionInteractor.vote(pollModel.getIdPoll(), votedPollOptionId, isPrivateVote,
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
    getStreamInteractor.loadStream(idStream, new GetStreamInteractor.Callback() {
      @Override public void onLoaded(Stream stream) {
        if (!isPollOwner() && !stream.isCurrentUserContributor()) {
          pollVoteView.showResultsWithoutVotingDialog();
        } else {
          pollVoteView.goToResults(pollModel.getIdPoll(), pollModel.getIdStream());
        }
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

  public String getIdStream() {
    if (pollModel != null) {
      return pollModel.getIdStream();
    } else {
      return null;
    }
  }

  public String getStreamTitle() {
    if (pollModel != null) {
      return pollModel.getStreamTitle();
    } else {
      return null;
    }
  }

  public void changeVotePrivacyToPublic() {
    isPrivateVote = false;
    pollVoteView.showPublicVotePrivacy();
  }

  public void changeVotePrivacyToPrivate() {
    isPrivateVote = true;
    pollVoteView.showPrivateVotePrivacy();
  }
}
