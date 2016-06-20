package com.shootr.mobile.domain.interactor.poll;

import com.shootr.mobile.domain.Poll;
import com.shootr.mobile.domain.PollOption;
import com.shootr.mobile.domain.PollStatus;
import com.shootr.mobile.domain.exception.PollDeletedException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.UserCannotVoteRequestException;
import com.shootr.mobile.domain.exception.UserHasVotedRequestException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.PollRepository;
import com.shootr.mobile.domain.repository.Remote;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class VotePollOptionInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final PollRepository localPollRepository;
  private final PollRepository remotePollRepository;
  private Callback<Poll> callback;
  private String idPoll;
  private String idPollOption;

  @Inject public VotePollOptionInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local PollRepository localPollRepository,
      @Remote PollRepository remotePollRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localPollRepository = localPollRepository;
    this.remotePollRepository = remotePollRepository;
  }

  public void vote(String idPoll, String idPollOption, Callback<Poll> callback) {
    this.idPoll = idPoll;
    this.idPollOption = idPollOption;
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      Poll poll = remotePollRepository.vote(idPoll, idPollOption);
      Collections.sort(poll.getPollOptions(), PollOption.PollOptionComparator);
      notifyLoaded(poll);
    } catch (ServerCommunicationException error) {
      fallbackToLocal();
    } catch (UserCannotVoteRequestException | UserHasVotedRequestException error) {
      notifyLocalPoll();
    }
  }

  private void notifyLocalPoll()
      throws UserCannotVoteRequestException, UserHasVotedRequestException,
      PollDeletedException {
    Poll poll = localPollRepository.getPollByIdPoll(idPoll);
    poll.setVoteStatus(PollStatus.VOTED);
    Collections.sort(poll.getPollOptions(), PollOption.PollOptionComparator);
    localPollRepository.putPoll(poll);
    notifyLoaded(poll);
  }

  private void fallbackToLocal()
      throws UserCannotVoteRequestException, UserHasVotedRequestException,
      PollDeletedException {
    Poll poll = localPollRepository.getPollByIdPoll(idPoll);
    List<PollOption> pollOptions = poll.getPollOptions();
    for (PollOption pollOption : pollOptions) {
      if (pollOption.getIdPollOption().equals(idPollOption)) {
        pollOption.setVotes(pollOption.getVotes() + 1);
        poll.setVoteStatus(PollStatus.VOTED);
        localPollRepository.putPoll(poll);
        break;
      }
    }
    Collections.sort(poll.getPollOptions(), PollOption.PollOptionComparator);
    notifyLoaded(poll);
  }

  private void notifyLoaded(final Poll poll) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(poll);
      }
    });
  }
}
