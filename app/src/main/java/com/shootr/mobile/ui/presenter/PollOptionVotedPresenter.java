package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.ui.Poller;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.ui.views.PollOptionVotedView;
import javax.inject.Inject;

public class PollOptionVotedPresenter implements Presenter {

  private static final long REFRESH_INTERVAL_MILLISECONDS_SEC = 1 * 1000;
  private static final long REFRESH_INTERVAL_MILLISECONDS_MIN = 60 * 1000;

  private final Poller poller;

  private PollOptionVotedView view;
  private PollOptionModel pollOptionVoted;
  private PollModel pollModel;

  @Inject public PollOptionVotedPresenter(Poller poller) {
    this.poller = poller;
  }

  protected void setView(PollOptionVotedView pollResultsView) {
    this.view = pollResultsView;
  }

  public void initialize(PollOptionVotedView pollResultsView, PollModel pollModel) {
    setView(pollResultsView);
    this.pollModel = pollModel;
    if (pollModel != null) {
      renderPollOptionVoted(pollModel);
      setupPoller();
    }
  }

  private void renderPollOptionVoted(PollModel pollModel) {
    setupFeedbackText(pollModel);
    for (PollOptionModel pollOptionModel : pollModel.getPollOptionModels()) {
      if (pollOptionModel.isVoted()) {
        this.pollOptionVoted = pollOptionModel;
        view.renderPollOptionVoted(pollOptionModel);
        break;
      }
    }
  }

  private void setupFeedbackText(PollModel pollModel) {
    if (pollModel.isDailyPoll() && pollModel.isVerifiedPoll()) {
      view.showSecureHiddenDailyText();
    } else if (pollModel.isDailyPoll() && !pollModel.isVerifiedPoll()) {
      view.showHiddenDailyText();
    } else if (pollModel.isVerifiedPoll()) {
      view.showLegalText();
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

  private void showPollVotesTimeToExpire(Long expirationDate) {
    view.showPollVotesTimeToExpire((expirationDate != null) ? expirationDate : -1,
        pollModel.isExpired());
  }

  public void shareVoted() {
    if (pollOptionVoted != null) {
      view.shareVoted(pollModel, pollOptionVoted);
    }
  }

  @Override public void resume() {

  }

  @Override public void pause() {
    poller.stopPolling();
  }
}
