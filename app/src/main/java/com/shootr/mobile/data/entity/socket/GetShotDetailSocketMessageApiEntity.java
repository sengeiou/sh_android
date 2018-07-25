package com.shootr.mobile.data.entity.socket;

import com.shootr.mobile.data.entity.PaginationEntity;

public class GetShotDetailSocketMessageApiEntity extends SocketMessageApiEntity {

  private ShotDetailParams data;

  public GetShotDetailSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.GET_SHOT_DETAIL);
  }

  public ShotDetailParams getData() {
    return data;
  }

  public void setData(ShotDetailParams data) {
    this.data = data;
  }

  public static class ShotDetailParams {
    String idShot;
    Pagination pagination;

    public String getIdShot() {
      return idShot;
    }

    public void setIdShot(String idShot) {
      this.idShot = idShot;
    }

    public Pagination getPagination() {
      return pagination;
    }

    public void setPagination(Pagination pagination) {
      this.pagination = pagination;
    }
  }

  public static class Pagination {
    PaginationEntity promoted;
    PaginationEntity subscribers;
    PaginationEntity basic;

    public PaginationEntity getPromoted() {
      return promoted;
    }

    public void setPromoted(PaginationEntity promoted) {
      this.promoted = promoted;
    }

    public PaginationEntity getSubscribers() {
      return subscribers;
    }

    public void setSubscribers(PaginationEntity subscribers) {
      this.subscribers = subscribers;
    }

    public PaginationEntity getBasic() {
      return basic;
    }

    public void setBasic(PaginationEntity basic) {
      this.basic = basic;
    }
  }
}
