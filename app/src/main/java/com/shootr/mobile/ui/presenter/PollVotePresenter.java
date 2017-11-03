package com.shootr.mobile.ui.presenter;

import android.content.Context;
import android.support.v4.app.NotificationManagerCompat;
import com.shootr.mobile.data.dagger.ApplicationContext;
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
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.service.user.UserCannotVoteDueToDeviceException;
import com.shootr.mobile.ui.Poller;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.ui.model.mappers.PollModelMapper;
import com.shootr.mobile.ui.views.PollVoteView;
import com.shootr.mobile.util.ErrorMessageFactory;
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
  private final Context appContext;

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
      ErrorMessageFactory errorMessageFactory, Poller poller,
      @ApplicationContext Context appContext) {
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
    this.appContext = appContext;
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
    getPollByIdPollInteractor.loadPollByIdPoll(idPoll, false, new Interactor.Callback<Poll>() {
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
      if (pollModel.isHideResults()) {
        pollVoteView.hideFooter();
        pollVoteView.hideShowResultsMenu();
      }
      showPollVotesTimeToExpire(pollModel.getExpirationDate());
      setupPoller();
      handlePollPrivacy();
    } else {
      handleNextScreen();
    }
  }

  private void handleNextScreen() {
    if (pollModel != null) {
      if (pollModel.isHideResults() && pollModel.getHasVoted()) {
        pollVoteView.goToVotedOption(pollModel);
      } else if (pollModel.isHideResults()) {
        pollVoteView.goToHiddenResults(pollModel.getQuestion());
      } else {
        pollVoteView.goToResults(pollModel.getIdPoll(), pollModel.getIdStream(), false);
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
    pollVoteView.showPollVotesTimeToExpire(pollVotes,
        (expirationDate != null) ? expirationDate : -1, pollModel.isExpired());
  }

  private void countPollVotes() {
    pollVotes = ZERO_VOTES;
    for (PollOptionModel pollOptionModel : pollModel.getPollOptionModels()) {
      pollVotes += pollOptionModel.getVotes();
    }
  }

  private void setupPoller() {
    if (pollModel != null) {
      this.poller.init(REFRESH_INTERVAL_MILLISECONDS_SEC, new Runnable() {
        @Override public void run() {
          showPollVotesTimeToExpire(pollModel.getExpirationDate());
          if (pollModel.isExpired()) {
            poller.stopPolling();
            pollVoteView.goToResults(pollModel.getIdPoll(), pollModel.getIdStream(), false);
          }
          if (!pollModel.isLessThanHourToExpire()
              && poller.getIntervalMilliseconds() != REFRESH_INTERVAL_MILLISECONDS_MIN) {
            poller.stopPolling();
            poller.setIntervalMilliseconds(REFRESH_INTERVAL_MILLISECONDS_MIN);
            poller.startPolling();
          } else if (pollModel.isLessThanHourToExpire()
              && poller.getIntervalMilliseconds() == REFRESH_INTERVAL_MILLISECONDS_MIN) {
            poller.stopPolling();
            poller.setIntervalMilliseconds(REFRESH_INTERVAL_MILLISECONDS_SEC);
            poller.startPolling();
          }
        }
      });
      poller.startPolling();
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

  public void voteOption(final String pollOptionId) {
    if (pollModel != null) {
      if (pollModel.isVerifiedPoll() && !NotificationManagerCompat.from(appContext)
          .areNotificationsEnabled()) {
        pollVoteView.showNotificationsScreen();
      } else {
        pollVoteView.showLoading();
        setVotedPollOption(pollOptionId);

        votePollOptionInteractor.vote(pollModel.getIdPoll(), pollOptionId, isPrivateVote,
            pollModel.isVerifiedPoll(), new Interactor.Callback<Poll>() {
              @Override public void onLoaded(Poll poll) {
                if (pollModel.isHideResults()) {
                  manageHideResults(poll, pollOptionId);
                } else {
                  pollVoteView.hideLoading();
                  pollVoteView.goToResults(pollModel.getIdPoll(), pollModel.getIdStream(), true);
                }
              }
            }, new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                pollVoteView.hideLoading();
                if (error instanceof UserCannotVoteDueToDeviceException) {
                  pollVoteView.showUserCannotVoteAlert();
                } else {
                  pollVoteView.showTimeoutAlert();
                }
              }
            });
      }
    }
  }

  private void manageHideResults(Poll poll, String pollOptionId) {
    for (PollOption pollOption : poll.getPollOptions()) {
      if (pollOption.getIdPollOption().equals(pollOptionId)) {
        pollOption.setVoted(true);
      }
    }
    pollVoteView.goToVotedOption(pollModelMapper.transform(poll));
  }

  public void retryVote() {
    pollVoteView.showLoading();
    votePollOptionInteractor.vote(pollModel.getIdPoll(), votedPollOptionId, isPrivateVote,
        pollModel.isVerifiedPoll(), new Interactor.Callback<Poll>() {
          @Override public void onLoaded(Poll poll) {
            pollVoteView.hideLoading();
            pollVoteView.goToResults(pollModel.getIdPoll(), pollModel.getIdStream(), true);
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            pollVoteView.hideLoading();
            if (error instanceof UserCannotVoteDueToDeviceException) {
              pollVoteView.showUserCannotVoteAlert();
            } else {
              pollVoteView.showTimeoutAlert();
            }
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
    poller.stopPolling();
  }

  public void viewResults() {
    pollVoteView.goToResults(pollModel.getIdPoll(), pollModel.getIdStream(), false);
  }

  public void onShowPollResults() {
    getStreamInteractor.loadStream(idStream, new GetStreamInteractor.Callback() {
      @Override public void onLoaded(Stream stream) {
        pollVoteView.showResultsWithoutVotingDialog();
      }
    });
  }

  public void showPollResultsWithoutVoting() {
    showPollResultsInteractor.showPollResults(pollModel.getIdPoll(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            pollVoteView.goToResults(pollModel.getIdPoll(), pollModel.getIdStream(), false);
          }
        });
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
