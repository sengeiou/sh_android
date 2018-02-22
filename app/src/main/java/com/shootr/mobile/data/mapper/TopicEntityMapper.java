package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.TopicEntity;
import com.shootr.mobile.domain.model.Topic;
import javax.inject.Inject;

public class TopicEntityMapper {

  private final EntitiesEntityMapper entitiesEntityMapper;

  @Inject public TopicEntityMapper(EntitiesEntityMapper entitiesEntityMapper) {
    this.entitiesEntityMapper = entitiesEntityMapper;
  }

  public Topic map(TopicEntity topicEntity) {

    Topic topic = new Topic();
    topic.setComment(topicEntity.getComment());
    topic.setEntities(entitiesEntityMapper.setupEntities(topicEntity.getEntities()));
    return topic;
  }

  public TopicEntity map(Topic topic) {
    TopicEntity topicEntity = new TopicEntity();
    topicEntity.setComment(topic.getComment());
    topicEntity.setEntities(entitiesEntityMapper.setupEntitiesEntity(topic.getEntities()));
    return topicEntity;
  }
}
