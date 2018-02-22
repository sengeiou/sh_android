package com.shootr.mobile.data.api.entity.mapper;

import com.shootr.mobile.data.api.entity.TopicApiEntity;
import com.shootr.mobile.data.entity.TopicEntity;
import javax.inject.Inject;

public class TopicApiEntityMapper {

  private final EntitiesApiEntityMapper entitiesApiEntityMapper;

  @Inject public TopicApiEntityMapper(EntitiesApiEntityMapper entitiesApiEntityMapper) {
    this.entitiesApiEntityMapper = entitiesApiEntityMapper;
  }

  public TopicEntity map(TopicApiEntity topicApiEntity) {

    TopicEntity topicEntity = new TopicEntity();
    topicEntity.setComment(topicApiEntity.getComment());
    topicEntity.setEntities(entitiesApiEntityMapper.setupEntities(topicApiEntity.getEntities()));
    return topicEntity;
  }
}
