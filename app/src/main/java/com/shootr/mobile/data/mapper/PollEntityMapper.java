package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.PollEntity;
import com.shootr.mobile.domain.model.poll.Poll;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PollEntityMapper {

  private final PollOptionEntityMapper mapper;

  @Inject public PollEntityMapper(PollOptionEntityMapper mapper) {
    this.mapper = mapper;
  }

  public Poll transform(PollEntity pollEntity) {
    Poll poll = new Poll();
    if (pollEntity != null) {
      poll.setStatus(pollEntity.getStatus());
      poll.setPublished(pollEntity.getPublished() != 0L);
      poll.setIdStream(pollEntity.getIdStream());
      poll.setIdPoll(pollEntity.getIdPoll());
      poll.setIdUser(pollEntity.getIdUser());
      poll.setHasVoted(pollEntity.getUserHasVoted());
      poll.setPollOptions(mapper.transform(pollEntity.getPollOptions()));
      poll.setQuestion(pollEntity.getQuestion());
      poll.setVoteStatus(pollEntity.getVoteStatus());
      poll.setVotePrivacy(pollEntity.getVotePrivacy());
      poll.setExpirationDate(pollEntity.getExpirationDate());
      poll.setVerifiedPoll(pollEntity.getVerifiedPoll() != null ? pollEntity.getVerifiedPoll() : false);
      poll.setHideResults(pollEntity.isHideResults());
    }
    return poll;
  }

  public PollEntity transform(Poll poll) {
    PollEntity pollEntity = new PollEntity();
    pollEntity.setPollOptions(mapper.transformDB(poll.getPollOptions()));
    pollEntity.setStatus(poll.getStatus());
    pollEntity.setQuestion(poll.getQuestion());
    pollEntity.setIdStream(poll.getIdStream());
    pollEntity.setIdUser(poll.getIdUser());
    pollEntity.setUserHasVoted(poll.hasVoted());
    pollEntity.setIdPoll(poll.getIdPoll());
    pollEntity.setPublished(poll.getPublished() ? 1L : 0L);
    pollEntity.setVoteStatus(poll.getVoteStatus());
    pollEntity.setVotePrivacy(poll.getVotePrivacy());
    pollEntity.setExpirationDate(poll.getExpirationDate());
    pollEntity.setVerifiedPoll(poll.isVerifiedPoll());
    pollEntity.setHideResults(poll.isHideResults());
    return pollEntity;
  }

  public List<Poll> transform(List<PollEntity> polls) {
    List<Poll> businessObjects = new ArrayList<>(polls.size());
    for (PollEntity poll : polls) {
      businessObjects.add(transform(poll));
    }
    return businessObjects;
  }
}
