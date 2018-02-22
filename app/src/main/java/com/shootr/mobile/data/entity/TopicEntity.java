package com.shootr.mobile.data.entity;

import com.shootr.mobile.domain.model.PrintableType;

public class TopicEntity implements PrintableItemEntity {

  private String comment;
  private EntitiesEntity entities;

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public EntitiesEntity getEntities() {
    return entities;
  }

  public void setEntities(EntitiesEntity entities) {
    this.entities = entities;
  }

  @Override public String getResultType() {
    return PrintableType.TOPIC;
  }
}
