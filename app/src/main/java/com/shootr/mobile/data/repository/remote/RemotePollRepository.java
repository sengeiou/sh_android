package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.PollEntity;
import com.shootr.mobile.data.entity.PollOptionEntity;
import com.shootr.mobile.data.mapper.PollEntityMapper;
import com.shootr.mobile.data.repository.datasource.poll.PollDataSource;
import com.shootr.mobile.domain.exception.PollDeletedException;
import com.shootr.mobile.domain.exception.UserCannotVoteDueToDeviceRequestException;
import com.shootr.mobile.domain.exception.UserCannotVoteRequestException;
import com.shootr.mobile.domain.exception.UserHasVotedRequestException;
import com.shootr.mobile.domain.model.poll.Poll;
import com.shootr.mobile.domain.model.poll.PollStatus;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.poll.ExternalPollRepository;
import java.util.List;
import javax.inject.Inject;

public class RemotePollRepository implements ExternalPollRepository {

  private final PollDataSource remotePollDataSource;
  private final PollDataSource localPollDataSource;
  private final PollEntityMapper pollEntityMapper;

  @Inject public RemotePollRepository(@Remote PollDataSource remotePollDataSource,
      @Local PollDataSource localPollDataSource, PollEntityMapper pollEntityMapper) {
    this.remotePollDataSource = remotePollDataSource;
    this.localPollDataSource = localPollDataSource;
    this.pollEntityMapper = pollEntityMapper;
  }

  @Override public List<Poll> getPollByIdStream(String idStream)
      throws UserCannotVoteRequestException, UserHasVotedRequestException, PollDeletedException {
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
      throws UserCannotVoteRequestException, UserHasVotedRequestException, PollDeletedException {
    PollEntity pollEntity = remotePollDataSource.getPollById(idPoll);
    setPollVoteStatus(pollEntity);
    localPollDataSource.putPoll(pollEntity);
    return pollEntityMapper.transform(pollEntity);
  }

  private void setPollVoteStatus(PollEntity pollEntity)
      throws UserCannotVoteRequestException, UserHasVotedRequestException, PollDeletedException {
    PollEntity pollById = localPollDataSource.getPollById(pollEntity.getIdPoll());
    if (pollById != null && pollById.getVoteStatus().equals(PollStatus.IGNORED)) {
      pollEntity.setVoteStatus(PollStatus.IGNORED);
    } else if (pollById != null && pollById.getVoteStatus().equals(PollStatus.HASSEENRESULTS)) {
      pollEntity.setVoteStatus(PollStatus.HASSEENRESULTS);
    } else if (pollById != null && pollById.getVoteStatus().equals(PollStatus.VOTED)) {
      pollEntity.setVoteStatus(PollStatus.VOTED);
      pollEntity.setUserHasVoted(true);
      overrideVotedOption(pollEntity, pollById);

    } else {
      pollEntity.setVoteStatus(pollEntity.getUserHasVoted() ? PollStatus.VOTED : PollStatus.VOTE);
    }
  }

  private void overrideVotedOption(PollEntity pollEntity, PollEntity pollById) {
    String votedOptionId = null;
    for (PollOptionEntity pollOptionEntity : pollById.getPollOptions()) {
      if (pollOptionEntity.isVoted()) {
        votedOptionId = pollOptionEntity.getIdPollOption();
      }
    }

    if (votedOptionId != null) {
      for (PollOptionEntity pollOptionEntity : pollEntity.getPollOptions()) {
        if (pollOptionEntity.getIdPollOption().equals(votedOptionId)) {
          pollOptionEntity.setVoted(true);
        }
      }
    }
  }

  @Override public Poll vote(String idPoll, String idPollOption, boolean isPrivateVote)
      throws UserCannotVoteRequestException, UserHasVotedRequestException,
      UserCannotVoteDueToDeviceRequestException {
    PollEntity pollEntity = remotePollDataSource.vote(idPoll, idPollOption, isPrivateVote);
    pollEntity.setVoteStatus(PollStatus.VOTED);
    storePollOptionVoted(idPollOption, pollEntity);
    localPollDataSource.putPoll(pollEntity);
    return pollEntityMapper.transform(pollEntity);
  }

  private void storePollOptionVoted(String idPollOption, PollEntity pollEntity) {
    for (PollOptionEntity pollOptionEntity : pollEntity.getPollOptions()) {
      if (pollOptionEntity.getIdPollOption().equals(idPollOption)) {
        pollOptionEntity.setVoted(true);
        break;
      }
    }
  }

  @Override public void sharePoll(String idPoll) throws PollDeletedException {
    remotePollDataSource.sharePoll(idPoll);
  }
}
