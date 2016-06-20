package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.PollEntity;
import com.shootr.mobile.data.mapper.PollEntityMapper;
import com.shootr.mobile.data.repository.datasource.poll.PollDataSource;
import com.shootr.mobile.domain.Poll;
import com.shootr.mobile.domain.PollStatus;
import com.shootr.mobile.domain.exception.PollDeletedException;
import com.shootr.mobile.domain.exception.UserCannotVoteRequestException;
import com.shootr.mobile.domain.exception.UserHasVotedRequestException;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.PollRepository;
import com.shootr.mobile.domain.repository.Remote;
import java.util.List;
import javax.inject.Inject;

public class RemotePollRepository implements PollRepository {

  private final PollDataSource remotePollDataSource;
  private final PollDataSource localPollDataSource;
  private final PollEntityMapper pollEntityMapper;

  @Inject
  public RemotePollRepository(@Remote PollDataSource remotePollDataSource,
      @Local PollDataSource localPollDataSource, PollEntityMapper pollEntityMapper) {
    this.remotePollDataSource = remotePollDataSource;
    this.localPollDataSource = localPollDataSource;
    this.pollEntityMapper = pollEntityMapper;
  }

  @Override public List<Poll> getPollByIdStream(String idStream)
      throws UserCannotVoteRequestException, UserHasVotedRequestException,
      PollDeletedException {
    List<PollEntity> polls = remotePollDataSource.getPolls(idStream);
    if (polls.size() > 0) {
      PollEntity pollEntity = polls.get(0);
      setPollVoteStatus(pollEntity);
      localPollDataSource.removePolls(idStream);
      localPollDataSource.putPoll(pollEntity);
    }
    return pollEntityMapper.transform(polls);
  }

  @Override public Poll getPollByIdPoll(String idPoll)
      throws UserCannotVoteRequestException, UserHasVotedRequestException,
      PollDeletedException {
    PollEntity pollEntity = remotePollDataSource.getPollById(idPoll);
    setPollVoteStatus(pollEntity);
    localPollDataSource.putPoll(pollEntity);
    return pollEntityMapper.transform(pollEntity);
  }

  private void setPollVoteStatus(PollEntity pollEntity)
      throws UserCannotVoteRequestException, UserHasVotedRequestException,
      PollDeletedException {
    PollEntity pollById = localPollDataSource.getPollById(pollEntity.getIdPoll());
    if (pollById != null) {
      pollEntity.setVoteStatus(pollById.getVoteStatus());
    }
  }

  @Override public void putPoll(Poll poll) {
    throw new IllegalArgumentException("method not implemented");
  }

  @Override public Poll vote(String idPoll, String idPollOption)
      throws UserCannotVoteRequestException, UserHasVotedRequestException {
    PollEntity pollEntity = remotePollDataSource.vote(idPoll, idPollOption);
    pollEntity.setVoteStatus(PollStatus.VOTED);
    localPollDataSource.putPoll(pollEntity);
    return pollEntityMapper.transform(pollEntity);
  }
}
