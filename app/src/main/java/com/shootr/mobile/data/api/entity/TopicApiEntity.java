package com.shootr.mobile.data.api.entity;

import com.shootr.mobile.domain.model.PrintableType;

public class TopicApiEntity extends PrintableItemApiEntity {

  private String comment;
  private BaseMessageEntitiesApiEntity entities;

  public TopicApiEntity() {
    setResultType(PrintableType.TOPIC);
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public BaseMessageEntitiesApiEntity getEntities() {
    return entities;
  }

  public void setEntities(BaseMessageEntitiesApiEntity entities) {
    this.entities = entities;
  }
}
