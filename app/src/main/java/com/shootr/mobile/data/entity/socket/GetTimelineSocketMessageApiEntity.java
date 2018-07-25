package com.shootr.mobile.data.entity.socket;

import com.shootr.mobile.data.entity.PaginationEntity;
import com.shootr.mobile.data.entity.ParamsEntity;

public class GetTimelineSocketMessageApiEntity extends SocketMessageApiEntity {

  TimelineParams data;

  public GetTimelineSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.GET_TIMELINE);
  }

  public TimelineParams getData() {
    return data;
  }

  public void setData(TimelineParams data) {
    this.data = data;
  }

  public static class TimelineParams {
    String idStream;
    String filter;
    PaginationEntity pagination;
    ParamsEntity params;

    public String getIdStream() {
      return idStream;
    }

    public void setIdStream(String idStream) {
      this.idStream = idStream;
    }

    public String getFilter() {
      return filter;
    }

    public void setFilter(String filter) {
      this.filter = filter;
    }

    public PaginationEntity getPagination() {
      return pagination;
    }

    public void setPagination(PaginationEntity pagination) {
      this.pagination = pagination;
    }

    public ParamsEntity getParams() {
      return params;
    }

    public void setParams(ParamsEntity params) {
      this.params = params;
    }
  }
}
