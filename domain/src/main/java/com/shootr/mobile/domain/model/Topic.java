package com.shootr.mobile.domain.model;

import com.shootr.mobile.domain.model.shot.Entities;

public class Topic implements PrintableItem {

  private String comment;
  private Entities entities;

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Entities getEntities() {
    return entities;
  }

  public void setEntities(Entities entities) {
    this.entities = entities;
  }

  @Override public String getResultType() {
    return PrintableType.TOPIC;
  }

  @Override public String getMessageType() {
    return null;
  }
}
