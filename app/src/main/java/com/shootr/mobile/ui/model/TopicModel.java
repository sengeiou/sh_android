package com.shootr.mobile.ui.model;

public class TopicModel implements PrintableModel, EntityContainable {

  private String comment;
  private EntitiesModel entitiesModel;
  private String timelineGroup;

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override public String getTimelineGroup() {
    return timelineGroup;
  }

  @Override public void setTimelineGroup(String timelineGroup) {
    this.timelineGroup = timelineGroup;
  }

  @Override public Long getOrder() {
    return 0L;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TopicModel that = (TopicModel) o;

    return comment != null ? comment.equals(that.comment) : that.comment == null;
  }

  @Override public EntitiesModel getEntitiesModel() {
    return entitiesModel;
  }

  public void setEntitiesModel(EntitiesModel entitiesModel) {
    this.entitiesModel = entitiesModel;
  }

  @Override public int hashCode() {
    return comment != null ? comment.hashCode() : 0;
  }
}
