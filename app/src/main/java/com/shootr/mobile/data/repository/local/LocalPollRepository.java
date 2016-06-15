package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.entity.PollEntity;
import com.shootr.mobile.data.mapper.PollEntityMapper;
import com.shootr.mobile.data.repository.datasource.poll.PollDataSource;
import com.shootr.mobile.domain.Poll;
import com.shootr.mobile.domain.exception.StreamTooManyPolls;
import com.shootr.mobile.domain.exception.UserCannotVoteRequestException;
import com.shootr.mobile.domain.exception.UserHasVotedRequestException;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.PollRepository;
import java.util.List;
import javax.inject.Inject;

public class LocalPollRepository implements PollRepository {

  private final PollDataSource pollDataSource;
  private final PollEntityMapper mapper;

  @Inject public LocalPollRepository(@Local PollDataSource pollDataSource, PollEntityMapper mapper) {
    this.pollDataSource = pollDataSource;
    this.mapper = mapper;
  }

  @Override public List<Poll> getPollByIdStream(String idStream)
      throws UserCannotVoteRequestException, StreamTooManyPolls, UserHasVotedRequestException {
    return mapper.transform(pollDataSource.getPolls(idStream));
  }

  @Override public void putPoll(Poll poll) {
    PollEntity pollEntity = mapper.transform(poll);
    pollDataSource.putPoll(pollEntity);
  }

  @Override public Poll getPollByIdPoll(String idPoll) {
    return mapper.transform(pollDataSource.getPollById(idPoll));
  }

  @Override public Poll vote(String idPoll, String idPollOption) {
    throw new IllegalArgumentException("method not implemented");
  }
}