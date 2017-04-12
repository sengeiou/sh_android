package com.shootr.mobile.data.entity;

import com.shootr.mobile.domain.model.SearchableType;

public class RecentSearchEntity {

  private Long visitDate;
  private StreamEntity stream;
  private UserEntity user;
  private String searchableType;

  public String getSearchableType() {
    return searchableType;
  }

  public void setSearchableType(String searchableType) {
    this.searchableType = searchableType;
  }

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
