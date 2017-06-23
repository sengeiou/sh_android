package com.shootr.mobile.domain.interactor.poll;

import com.shootr.mobile.domain.exception.PollDeletedException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.UserCannotVoteDueToDeviceRequestException;
import com.shootr.mobile.domain.exception.UserCannotVoteRequestException;
import com.shootr.mobile.domain.exception.UserHasVotedRequestException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.poll.Poll;
import com.shootr.mobile.domain.model.poll.PollOption;
import com.shootr.mobile.domain.model.poll.PollStatus;
import com.shootr.mobile.domain.repository.poll.ExternalPollRepository;
import com.shootr.mobile.domain.repository.poll.InternalPollRepository;
import com.shootr.mobile.domain.service.user.UserCannotVoteDueToDeviceException;
import java.util.Collections;
import javax.inject.Inject;

public class VotePollOptionInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final InternalPollRepository localPollRepository;
  private final ExternalPollRepository remotePollRepository;
  private Callback<Poll> callback;
  private ErrorCallback errorCallback;
  private String idPoll;
  private String idPollOption;
  private boolean isPrivateVote;
  private boolean isSecurePoll;

  @Inject public VotePollOptionInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, InternalPollRepository localPollRepository,
      ExternalPollRepository remotePollRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localPollRepository = localPollRepository;
    this.remotePollRepository = remotePollRepository;
  }

  public void vote(String idPoll, String idPollOption, boolean isPrivateVote, boolean isSecurePoll,
      Callback<Poll> callback, ErrorCallback errorCallback) {
    this.idPoll = idPoll;
    this.idPollOption = idPollOption;
    this.isPrivateVote = isPrivateVote;
    this.isSecurePoll = isSecurePoll;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      Poll poll = remotePollRepository.vote(idPoll, idPollOption, isPrivateVote);
      Collections.sort(poll.getPollOptions(), PollOption.PollOptionComparator);
      notifyLoaded(poll);
    } catch (ServerCommunicationException error) {
      notifyError(error);
    } catch (UserHasVotedRequestException error) {
      if (isSecurePoll) {
        notifyError(new UserCannotVoteDueToDeviceException(error));
      } else {
        notifyLocalPoll();
      }
    } catch (UserCannotVoteRequestException error) {
      notifyLocalPoll();
    } catch (UserCannotVoteDueToDeviceRequestException error) {
      notifyError(new UserCannotVoteDueToDeviceException(error));
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

  private void notifyLoaded(final Poll poll) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(poll);
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
