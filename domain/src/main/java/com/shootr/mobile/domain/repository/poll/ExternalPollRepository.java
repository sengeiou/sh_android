package com.shootr.mobile.domain.repository.poll;

import com.shootr.mobile.domain.exception.PollDeletedException;
import com.shootr.mobile.domain.exception.UserCannotVoteRequestException;
import com.shootr.mobile.domain.exception.UserHasVotedRequestException;
import com.shootr.mobile.domain.model.poll.Poll;

public interface ExternalPollRepository extends PollRepository {

  Poll vote(String idPoll, String idPollOption)
      throws UserCannotVoteRequestException, UserHasVotedRequestException;

  void sharePoll(String idPoll) throws PollDeletedException;
}
