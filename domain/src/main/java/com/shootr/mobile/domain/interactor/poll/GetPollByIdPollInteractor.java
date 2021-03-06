package com.shootr.mobile.domain.interactor.poll;

import com.shootr.mobile.domain.exception.PollDeletedException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.UserCannotVoteRequestException;
import com.shootr.mobile.domain.exception.UserHasVotedRequestException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.poll.Poll;
import com.shootr.mobile.domain.model.poll.PollOption;
import com.shootr.mobile.domain.repository.poll.ExternalPollRepository;
import com.shootr.mobile.domain.repository.poll.InternalPollRepository;
import com.shootr.mobile.domain.repository.poll.PollRepository;
import com.shootr.mobile.domain.service.PollHasBeenDeletedException;
import com.shootr.mobile.domain.service.UserCannotVoteException;
import com.shootr.mobile.domain.service.user.UserHasVotedException;
import java.util.Collections;
import javax.inject.Inject;

public class GetPollByIdPollInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final InternalPollRepository localPollRepository;
  private final ExternalPollRepository remotePollRepository;
  private Callback<Poll> callback;
  private ErrorCallback errorCallback;
  private String idPoll;
  private boolean hasVoted;

  @Inject public GetPollByIdPollInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, InternalPollRepository localPollRepository,
      ExternalPollRepository remotePollRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localPollRepository = localPollRepository;
    this.remotePollRepository = remotePollRepository;
  }

  public void loadPollByIdPoll(String idPoll, boolean hasVoted, Callback<Poll> callback,
      ErrorCallback errorCallback) {
    this.idPoll = idPoll;
    this.callback = callback;
    this.hasVoted = hasVoted;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      obtainPollFromRepository(hasVoted ? localPollRepository : remotePollRepository);
    } catch (ServerCommunicationException error) {
      obtainPollFromRepository(localPollRepository);
      notifyError(error);
    }
  }

  private void obtainPollFromRepository(PollRepository pollRepository) {
    try {
      Poll poll = pollRepository.getPollByIdPoll(idPoll);
      if (poll != null && poll.getPollOptions() != null) {
        Collections.sort(poll.getPollOptions(), PollOption.PollOptionComparator);
        notifyLoaded(poll);
      } else {
        notifyLoaded(null);
      }
    } catch (UserCannotVoteRequestException error) {
      notifyError(new UserCannotVoteException(error));
    } catch (UserHasVotedRequestException error) {
      notifyError(new UserHasVotedException(error));
    } catch (PollDeletedException e) {
      notifyError(new PollHasBeenDeletedException(e));
    }
  }

  private void notifyLoaded(final Poll result) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(result);
      }
    });
  }

  protected void notifyError(final ShootrException error) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        errorCallback.onError(error);
      }
    });
  }
}
