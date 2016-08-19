package com.shootr.mobile.domain.interactor.poll;

import com.shootr.mobile.domain.model.poll.Poll;
import com.shootr.mobile.domain.model.poll.PollStatus;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.poll.InternalPollRepository;
import javax.inject.Inject;

public class ShowPollResultsInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final InternalPollRepository localPollRepository;

  private String idPoll;
  private CompletedCallback completedCallback;

  @Inject ShowPollResultsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      InternalPollRepository localPollRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localPollRepository = localPollRepository;
  }

  public void showPollResults(String idPoll, CompletedCallback completedCallback) {
    this.idPoll = idPoll;
    this.completedCallback = completedCallback;
    this.interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    Poll poll = localPollRepository.getPollByIdPoll(idPoll);
    poll.setVoteStatus(PollStatus.HASSEENRESULTS);
    localPollRepository.putPoll(poll);
    notifyCompleted();
  }

  private void notifyCompleted() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        completedCallback.onCompleted();
      }
    });
  }
}
