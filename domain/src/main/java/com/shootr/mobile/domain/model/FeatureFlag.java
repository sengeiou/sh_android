package com.shootr.mobile.domain.model;

public class FeatureFlag {

  private long birth;
  private long modified;
  private int revision;
  private String description;
  private String type;

  public long getBirth() {
    return birth;
  }

  public void setBirth(long birth) {
    this.birth = birth;
  }

  public long getModified() {
    return modified;
  }

  public void setModified(long modified) {
    this.modified = modified;
  }

  public int getRevision() {
    return revision;
  }

  public void setRevision(int revision) {
    this.revision = revision;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
