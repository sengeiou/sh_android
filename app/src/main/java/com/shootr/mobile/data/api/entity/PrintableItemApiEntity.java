package com.shootr.mobile.data.api.entity;

import com.shootr.mobile.data.entity.FollowableEntity;

public class PrintableItemApiEntity extends FollowableEntity {

  private String resultType;

  public String getResultType() {
    return resultType;
  }

  public void setResultType(String resultType) {
    this.resultType = resultType;
  }
}
