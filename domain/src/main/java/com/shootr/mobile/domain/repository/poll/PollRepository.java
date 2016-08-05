package com.shootr.mobile.domain.repository.poll;

import com.shootr.mobile.domain.model.poll.Poll;
import com.shootr.mobile.domain.exception.PollDeletedException;
import com.shootr.mobile.domain.exception.UserCannotVoteRequestException;
import com.shootr.mobile.domain.exception.UserHasVotedRequestException;
import java.util.List;

public interface PollRepository {

  List<Poll> getPollByIdStream(String idStream)
      throws UserCannotVoteRequestException, UserHasVotedRequestException,
      PollDeletedException;

  Poll getPollByIdPoll(String idPoll)
      throws UserCannotVoteRequestException, UserHasVotedRequestException,
      PollDeletedException;
}
