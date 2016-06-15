package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.Poll;
import com.shootr.mobile.domain.exception.StreamTooManyPolls;
import com.shootr.mobile.domain.exception.UserCannotVoteRequestException;
import com.shootr.mobile.domain.exception.UserHasVotedRequestException;
import java.util.List;

public interface PollRepository {

  List<Poll> getPollByIdStream(String idStream)
      throws UserCannotVoteRequestException, StreamTooManyPolls, UserHasVotedRequestException;

  void putPoll(Poll poll);

  Poll getPollByIdPoll(String idPoll);

  Poll vote(String idPoll, String idPollOption)
      throws UserCannotVoteRequestException, UserHasVotedRequestException;
}
