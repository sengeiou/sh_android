package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.poll.PollOption;
import com.shootr.mobile.ui.model.PollOptionModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PollOptionModelMapper {

  @Inject public PollOptionModelMapper() {
  }

  public PollOptionModel transform(PollOption pollOption) {
    if (pollOption == null) {
      return null;
    }

    PollOptionModel pollOptionModel = new PollOptionModel();
    pollOptionModel.setIdPoll(pollOption.getIdPoll());
    pollOptionModel.setIdPollOption(pollOption.getIdPollOption());
    pollOptionModel.setText(pollOption.getTitle());
    pollOptionModel.setImageUrl(pollOption.getImageUrl());
    pollOptionModel.setVotes(pollOption.getVotes());

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
