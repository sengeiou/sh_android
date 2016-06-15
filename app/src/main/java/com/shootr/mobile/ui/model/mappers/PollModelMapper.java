package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.Poll;
import com.shootr.mobile.domain.PollStatus;
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
    if (poll == null) {
      return null;
    }

    PollModel pollModel = new PollModel();
    pollModel.setIdPoll(poll.getIdPoll());
    pollModel.setIdStream(poll.getIdStream());
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
