package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.Topic;
import com.shootr.mobile.ui.model.TopicModel;
import javax.inject.Inject;

public class TopicModelMapper {

  private final EntitiesModelMapper entitiesModelMapper;

  @Inject public TopicModelMapper(EntitiesModelMapper entitiesModelMapper) {
    this.entitiesModelMapper = entitiesModelMapper;
  }

  public TopicModel map(Topic topic) {
    TopicModel topicModel = new TopicModel();
    topicModel.setComment(topic.getComment());
    topicModel.setEntitiesModel(entitiesModelMapper.setupEntities(topic.getEntities()));
    return topicModel;
  }
}
