package com.shootr.mobile.data.repository.datasource.poll;

import com.shootr.mobile.data.entity.PollEntity;
import com.shootr.mobile.domain.exception.StreamTooManyPolls;
import com.shootr.mobile.domain.exception.UserCannotVoteRequestException;
import com.shootr.mobile.domain.exception.UserHasVotedRequestException;
import java.util.List;

public interface PollDataSource {

  List<PollEntity> getPolls(String idStream)
      throws UserCannotVoteRequestException, UserHasVotedRequestException, StreamTooManyPolls;

  void putPoll(PollEntity poll);

  PollEntity getPollById(String idPoll);

  void removePolls(String idStream);

  PollEntity vote(String idPoll, String idPollOption)
      throws UserCannotVoteRequestException, UserHasVotedRequestException;
}
