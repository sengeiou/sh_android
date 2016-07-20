package com.shootr.mobile.data.repository.datasource.poll;

import com.shootr.mobile.data.entity.PollEntity;
import com.shootr.mobile.domain.exception.PollDeletedException;
import com.shootr.mobile.domain.exception.UserCannotVoteRequestException;
import com.shootr.mobile.domain.exception.UserHasVotedRequestException;
import java.util.List;

public interface PollDataSource {

  List<PollEntity> getPolls(String idStream)
      throws UserCannotVoteRequestException, UserHasVotedRequestException,
      PollDeletedException;

  void putPoll(PollEntity poll);

  PollEntity getPollById(String idPoll)
      throws UserCannotVoteRequestException, UserHasVotedRequestException,
      PollDeletedException;

  void removePolls(String idStream);

  PollEntity vote(String idPoll, String idPollOption)
      throws UserCannotVoteRequestException, UserHasVotedRequestException;

  void sharePoll(String idPoll) throws PollDeletedException;
}
