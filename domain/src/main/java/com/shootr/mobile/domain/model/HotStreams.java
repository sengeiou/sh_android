package com.shootr.mobile.domain.model;

import com.shootr.mobile.domain.model.stream.Stream;
import java.util.ArrayList;

public class HotStreams {

  private Pagination pagination;
  private ArrayList<Stream> streams;

  public Pagination getPagination() {
    return pagination;
  }

  public void setPagination(Pagination pagination) {
    this.pagination = pagination;
  }

  public ArrayList<Stream> getStreams() {
    return streams;
  }

  public void setStreams(ArrayList<Stream> streams) {
    this.streams = streams;
  }

  @Override public String toString() {
    return "HotStreams{" + "pagination=" + pagination + ", streams=" + streams + '}';
  }
}
