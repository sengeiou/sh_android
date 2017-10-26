package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.poll.PollOption;
import com.shootr.mobile.ui.model.PollOptionModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PollOptionModelMapper {

  private final ImageMediaModelMapper imageMediaModelMapper;

  @Inject public PollOptionModelMapper(ImageMediaModelMapper imageMediaModelMapper) {
    this.imageMediaModelMapper = imageMediaModelMapper;
  }

  public PollOptionModel transform(PollOption pollOption) {
    if (pollOption == null) {
      return null;
    }

    PollOptionModel pollOptionModel = new PollOptionModel();
    pollOptionModel.setIdPoll(pollOption.getIdPoll());
    pollOptionModel.setIdPollOption(pollOption.getIdPollOption());
    pollOptionModel.setText(pollOption.getTitle());
    pollOptionModel.setVotes(pollOption.getVotes());
    pollOptionModel.setVoted(pollOption.isVoted());
    pollOptionModel.setOptionImage(imageMediaModelMapper.transform(pollOption.getOptionImage()));

    return pollOptionModel;
  }

  public List<PollOptionModel> transform(List<PollOption> pollOptions) {
    ArrayList<PollOptionModel> pollOptionModels = new ArrayList<>();
    for (PollOption pollOption : pollOptions) {
      pollOptionModels.add(transform(pollOption));
    }
    return pollOptionModels;
  }
}
