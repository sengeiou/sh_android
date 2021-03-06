package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.poll.GetPollByIdPollInteractor;
import com.shootr.mobile.domain.interactor.poll.IgnorePollInteractor;
import com.shootr.mobile.domain.interactor.poll.SharePollInteractor;
import com.shootr.mobile.domain.model.poll.Poll;
import com.shootr.mobile.domain.model.poll.PollOption;
import com.shootr.mobile.domain.model.poll.PollStatus;
import com.shootr.mobile.ui.Poller;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.ui.model.mappers.PollModelMapper;
import com.shootr.mobile.ui.views.PollResultsView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.Collections;
import javax.inject.Inject;

public class PollResultsPresenter implements Presenter {

  private static final long ZERO_VOTES = 0;
  private static final long REFRESH_INTERVAL_MILLISECONDS_SEC = 1 * 1000;
  private static final long REFRESH_INTERVAL_MILLISECONDS_MIN = 60 * 1000;
  private final GetPollByIdPollInteractor getPollByIdPollInteractor;
  private final PollModelMapper pollModelMapper;
  private final IgnorePollInteractor ignorePollInteractor;
  private final SharePollInteractor sharePollInteractor;
  private final ErrorMessageFactory errorMessageFactory;
  private final Poller poller;

  private PollResultsView pollResultsView;
  private boolean hasBeenPaused;
  private String idPoll;
  private long pollVotes = ZERO_VOTES;
  private PollModel pollModel;
  private PollOptionModel pollOptionVoted;
  private String idStream;

  @Inject public PollResultsPresenter(GetPollByIdPollInteractor getPollByIdPollInteractor,
      PollModelMapper pollModelMapper, IgnorePollInteractor ignorePollInteractor,
      SharePollInteractor sharePollInteractor, ErrorMessageFactory errorMessageFactory,
      Poller poller) {
    this.getPollByIdPollInteractor = getPollByIdPollInteractor;
    this.pollModelMapper = pollModelMapper;
    this.ignorePollInteractor = ignorePollInteractor;
    this.sharePollInteractor = sharePollInteractor;
    this.errorMessageFactory = errorMessageFactory;
    this.poller = poller;
  }

  public void initialize(PollResultsView pollResultsView, String idPoll, String idStream,
      boolean hasVoted) {
    setView(pollResultsView);
    this.idPoll = idPoll;
    this.idStream = idStream;
    loadPoll(hasVoted);
  }

  protected void setView(PollResultsView pollResultsView) {
    this.pollResultsView = pollResultsView;
  }

  private void loadPoll(boolean hasVoted) {
    pollResultsView.showLoading();
    getPollByIdPollInteractor.loadPollByIdPoll(idPoll, hasVoted, new Interactor.Callback<Poll>() {
      @Override public void onLoaded(Poll poll) {
        orderPollOptions(poll);
        handlePollModel(poll);
        pollResultsView.hideLoading();
        if (poll.getStatus().equals(PollStatus.CLOSED)) {
          pollResultsView.showClosed();
        }
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        pollResultsView.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }

  private void handlePollModel(Poll poll) {
    PollModel pollModel = pollModelMapper.transform(poll);
    this.pollModel = pollModel;
    if (pollModel != null) {
      idPoll = pollModel.getIdPoll();
      pollResultsView.renderPollResults(pollModel, shouldShowShareVoted(pollModel));
      if (pollModel.isHideResults()) {
        pollResultsView.hideFooter();
      }
      if (pollModel.isDailyPoll()) {
        pollResultsView.showDailyPollText();
      }
      showPollVotesTimeToExpire(pollModel.getExpirationDate());
      setupPoller();
    }
  }

  private boolean shouldShowShareVoted(PollModel pollModel) {
    for (PollOptionModel pollOptionModel : pollModel.getPollOptionModels()) {
      if (pollOptionModel.isVoted()) {
        pollOptionVoted = pollOptionModel;
        return true;
      }
    }
    return false;
  }

  private void orderPollOptions(Poll poll) {
    if (poll.getPollOptions() != null) {
      Collections.sort(poll.getPollOptions(), PollOption.PollOptionVotesComparator);
    }
  }

  private void showPollVotesTimeToExpire(Long expirationDate) {
    countPollVotes();
    pollResultsView.showPollVotesTimeToExpire(pollVotes,
        (expirationDate != null) ? expirationDate : -1, pollModel.isExpired());
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
        } else if (pollModel.isExpired()) {
          poller.stopPolling();
        }
      }
    });
    poller.startPolling();
  }

  @Override public void resume() {
    if (hasBeenPaused) {
      pollVotes = ZERO_VOTES;
      loadPoll(false);
    }
  }

  @Override public void pause() {
    hasBeenPaused = true;
    poller.stopPolling();
  }

  public void ignorePoll() {
    ignorePollInteractor.ignorePoll(idPoll, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        pollResultsView.ignorePoll();
      }
    });
  }

  public void shareViaShootr() {
    sharePollInteractor.sharePoll(idPoll, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        pollResultsView.showSharedPoll();
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        pollResultsView.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }

  public void share() {
    pollResultsView.share(pollModel);
  }

  public void shareVoted() {
    if (pollOptionVoted != null) {
      pollResultsView.shareVoted(pollModel, pollOptionVoted);
    }
  }

  public void onStreamTitleClick() {
    if (idStream != null) {
      pollResultsView.goToStreamTimeline(idStream);
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
}
