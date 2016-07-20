package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.Poll;
import com.shootr.mobile.domain.PollOption;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.poll.GetPollByIdPollInteractor;
import com.shootr.mobile.domain.interactor.poll.IgnorePollInteractor;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.ui.model.mappers.PollModelMapper;
import com.shootr.mobile.ui.views.PollResultsView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.Collections;
import javax.inject.Inject;

public class PollResultsPresenter implements Presenter {

  private static final long ZERO_VOTES = 0;
  private final GetPollByIdPollInteractor getPollByIdPollInteractor;
  private final PollModelMapper pollModelMapper;
  private final IgnorePollInteractor ignorePollInteractor;
  private final ErrorMessageFactory errorMessageFactory;

  private PollResultsView pollResultsView;
  private boolean hasBeenPaused;
  private String idPoll;
  private long pollVotes = ZERO_VOTES;
  private PollModel pollModel;

  @Inject public PollResultsPresenter(GetPollByIdPollInteractor getPollByIdPollInteractor,
      PollModelMapper pollModelMapper, IgnorePollInteractor ignorePollInteractor,
      ErrorMessageFactory errorMessageFactory) {
    this.getPollByIdPollInteractor = getPollByIdPollInteractor;
    this.pollModelMapper = pollModelMapper;
    this.ignorePollInteractor = ignorePollInteractor;
    this.errorMessageFactory = errorMessageFactory;
  }

  public void initialize(PollResultsView pollResultsView, String idPoll) {
    this.pollResultsView = pollResultsView;
    this.idPoll = idPoll;
    loadPoll();
  }

  private void loadPoll() {
    pollResultsView.showLoading();
    getPollByIdPollInteractor.loadPollByIdPoll(idPoll, new Interactor.Callback<Poll>() {
      @Override public void onLoaded(Poll poll) {
        orderPollOptions(poll);
        handlePollModel(poll);
        pollResultsView.hideLoading();
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        pollResultsView.showError(errorMessageFactory.getMessageForError(error));
      }
    });

  }

  private void handlePollModel(Poll poll) {
    pollModel = pollModelMapper.transform(poll);
    if (pollModel != null) {
      idPoll = pollModel.getIdPoll();
      pollResultsView.renderPollResults(pollModel);
      showPollVotes();
    }
  }

  private void orderPollOptions(Poll poll) {
    if (poll.getPollOptions() != null) {
      Collections.sort(poll.getPollOptions(), PollOption.PollOptionVotesComparator);
    }
  }

  private void showPollVotes() {
    countPollVotes();
    pollResultsView.showPollVotes(pollVotes);
  }

  private void countPollVotes() {
    pollVotes = ZERO_VOTES;
    for (PollOptionModel pollOptionModel : pollModel.getPollOptionModels()) {
      pollVotes += pollOptionModel.getVotes();
    }
  }

  @Override public void resume() {
    if (hasBeenPaused) {
      pollVotes = ZERO_VOTES;
      loadPoll();
    }
  }

  @Override public void pause() {
    hasBeenPaused = true;
  }

  public void ignorePoll() {
    ignorePollInteractor.ignorePoll(idPoll, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        pollResultsView.ignorePoll();
      }
    });
  }
}
