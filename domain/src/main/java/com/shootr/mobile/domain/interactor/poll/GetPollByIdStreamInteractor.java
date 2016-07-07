package com.shootr.mobile.domain.interactor.poll;

import com.shootr.mobile.domain.Poll;
import com.shootr.mobile.domain.PollOption;
import com.shootr.mobile.domain.exception.PollDeletedException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.UserCannotVoteRequestException;
import com.shootr.mobile.domain.exception.UserHasVotedRequestException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.PollRepository;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.service.PollHasBeenDeletedException;
import com.shootr.mobile.domain.service.UserCannotVoteException;
import com.shootr.mobile.domain.service.user.UserHasVotedException;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetPollByIdStreamInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final PollRepository localPollRepository;
  private final PollRepository remotePollRepository;
  private Callback<Poll> callback;
  private ErrorCallback errorCallback;
  private String idStream;

  @Inject public GetPollByIdStreamInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local PollRepository localPollRepository,
      @Remote PollRepository remotePollRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localPollRepository = localPollRepository;
    this.remotePollRepository = remotePollRepository;
  }

  public void loadPoll(String idStream, Callback<Poll> callback, ErrorCallback errorCallback) {
    this.idStream = idStream;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    obtainPollFromRepository(localPollRepository);
    try {
      obtainPollFromRepository(remotePollRepository);
    } catch (ServerCommunicationException error) {
      notifyError(error);
    }
  }

  private void obtainPollFromRepository(PollRepository pollRepository) {
    try {
      List<Poll> polls = pollRepository.getPollByIdStream(idStream);
      if (polls != null && polls.size() > 0) {
        Poll poll = polls.get(0);
        if (poll != null && poll.getPollOptions() != null) {
          Collections.sort(poll.getPollOptions(), PollOption.PollOptionComparator);
        }
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
