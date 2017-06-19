package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.poll.Poll;
import com.shootr.mobile.domain.model.poll.PollStatus;
import com.shootr.mobile.ui.model.PollModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PollModelMapper {

  private PollOptionModelMapper mapper;

  @Inject public PollModelMapper(PollOptionModelMapper mapper) {
    this.mapper = mapper;
  }

  public PollModel transform(Poll poll) {
    if (poll == null || poll.getIdPoll() == null) {
      return null;
    }

    PollModel pollModel = new PollModel();
    pollModel.setIdPoll(poll.getIdPoll());
    pollModel.setIdStream(poll.getIdStream());
    pollModel.setIdUser(poll.getIdUser());
    pollModel.setQuestion(poll.getQuestion());
    pollModel.setStatus(poll.getStatus());
    pollModel.setPublished(poll.getPublished());
    pollModel.setHasVoted(poll.hasVoted());
    if (poll.getVoteStatus() == null) {
      pollModel.setVoteStatus(PollStatus.VOTE);
    } else {
      pollModel.setVoteStatus(poll.getVoteStatus());
    }
    if (poll.getPollOptions() != null) {
      pollModel.setPollOptionModels(mapper.transform(poll.getPollOptions()));
    }
    pollModel.setVotePrivacy(poll.getVotePrivacy() != null ? poll.getVotePrivacy() : PollStatus.PRIVATE);
    pollModel.setExpirationDate(poll.getExpirationDate());
    return pollModel;
  }

  public List<PollModel> transform(List<Poll> polls) {
    ArrayList<PollModel> pollModels = new ArrayList<>();
    for (Poll poll : polls) {
      pollModels.add(transform(poll));
    }
    return pollModels;
  }
}
