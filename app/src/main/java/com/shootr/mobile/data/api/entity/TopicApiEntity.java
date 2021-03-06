package com.shootr.mobile.data.api.entity;

import com.shootr.mobile.domain.model.PrintableType;

public class TopicApiEntity implements PrintableItemApiEntity {

  private String resultType;
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

  @Override public String getResultType() {
    return resultType;
  }

  @Override public void setResultType(String resultType) {
    this.resultType = resultType;
  }
}
