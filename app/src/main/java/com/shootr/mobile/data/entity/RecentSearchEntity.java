package com.shootr.mobile.data.entity;

public class RecentSearchEntity {

  private Long visitDate;
  private StreamEntity stream;
  private UserEntity user;
  public static final String STREAM = "0";
  public static final String USER = "1";

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  public StreamEntity getStream() {
    return stream;
  }

  public void setStream(StreamEntity stream) {
    this.stream = stream;
  }

  public Long getVisitDate() {
    return visitDate;
  }

  public void setVisitDate(Long visitDate) {
    this.visitDate = visitDate;
  }
}
