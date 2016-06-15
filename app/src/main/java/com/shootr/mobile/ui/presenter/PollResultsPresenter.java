package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.Poll;
import com.shootr.mobile.domain.PollOption;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.poll.GetPollByIdStreamInteractor;
import com.shootr.mobile.domain.interactor.poll.IgnorePollInteractor;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.mappers.PollModelMapper;
import com.shootr.mobile.ui.views.PollResultsView;
import java.util.Collections;
import javax.inject.Inject;

public class PollResultsPresenter implements Presenter {

  private final GetPollByIdStreamInteractor getPollByIdStreamInteractor;
  private final PollModelMapper pollModelMapper;
  private final IgnorePollInteractor ignorePollInteractor;

  private PollResultsView pollResultsView;
  private String idStream;
  private boolean hasBeenPaused;
  private String idPoll;

  @Inject public PollResultsPresenter(GetPollByIdStreamInteractor getPollByIdStreamInteractor,
      PollModelMapper pollModelMapper, IgnorePollInteractor ignorePollInteractor) {
    this.getPollByIdStreamInteractor = getPollByIdStreamInteractor;
    this.pollModelMapper = pollModelMapper;
    this.ignorePollInteractor = ignorePollInteractor;
  }

  public void initialize(PollResultsView pollResultsView, String idStream) {
    this.pollResultsView = pollResultsView;
    this.idStream = idStream;
    loadPoll();
  }

  private void loadPoll() {
    getPollByIdStreamInteractor.loadPoll(idStream, new Interactor.Callback<Poll>() {
      @Override public void onLoaded(Poll poll) {
        Collections.sort(poll.getPollOptions(), PollOption.PollOptionVotesComparator);
        PollModel pollModel = pollModelMapper.transform(poll);
        idPoll = pollModel.getIdPoll();
        pollResultsView.renderPollResults(pollModel);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        pollResultsView.showError(error.getMessage());
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

  public void ignorePoll() {
    ignorePollInteractor.ignorePoll(idPoll, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        pollResultsView.ignorePoll();
      }
    });
  }
}
