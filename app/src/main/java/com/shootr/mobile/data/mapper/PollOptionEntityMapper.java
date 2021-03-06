package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.PollOptionEntity;
import com.shootr.mobile.domain.model.poll.PollOption;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PollOptionEntityMapper {

  private final ImageMediaEntityMapper imageMediaEntityMapper;

  @Inject public PollOptionEntityMapper(ImageMediaEntityMapper imageMediaEntityMapper) {
    this.imageMediaEntityMapper = imageMediaEntityMapper;
  }

  public PollOption transform(PollOptionEntity pollOptionEntity) {
    PollOption pollOption = new PollOption();
    pollOption.setTitle(pollOptionEntity.getText());
    pollOption.setVotes(pollOptionEntity.getVotes());
    pollOption.setIdPoll(pollOptionEntity.getIdPoll());
    pollOption.setIdPollOption(pollOptionEntity.getIdPollOption());
    pollOption.setOrder(pollOptionEntity.getOrder());
    pollOption.setVoted(pollOptionEntity.isVoted());
    pollOption.setOptionImage(imageMediaEntityMapper.transform(pollOptionEntity.getOptionImage()));
    return pollOption;
  }

  public List<PollOption> transform(List<PollOptionEntity> pollOptions) {
    List<PollOption> options = new ArrayList<>();
    if (pollOptions != null) {
      for (PollOptionEntity pollOption : pollOptions) {
        if (pollOption.getText() != null) {
          options.add(transform(pollOption));
        }
      }
    }
    return options;
  }

  private PollOptionEntity transform(PollOption pollOption) {
    PollOptionEntity pollOptionEntity = new PollOptionEntity();
    pollOptionEntity.setOrder(pollOption.getOrder());
    pollOptionEntity.setIdPoll(pollOption.getIdPoll());
    pollOptionEntity.setText(pollOption.getTitle());
    pollOptionEntity.setVotes(pollOption.getVotes());
    pollOptionEntity.setIdPollOption(pollOption.getIdPollOption());
    pollOptionEntity.setVoted(pollOption.isVoted());
    pollOptionEntity.setOptionImage(imageMediaEntityMapper.transform(pollOption.getOptionImage()));
    return pollOptionEntity;
  }

  public List<PollOptionEntity> transformDB(List<PollOption> pollOptions) {
    List<PollOptionEntity> pollOptionEntities = new ArrayList<>(pollOptions.size());
    for (PollOption pollOption : pollOptions) {
      pollOptionEntities.add(transform(pollOption));
    }
    return pollOptionEntities;
  }
}
