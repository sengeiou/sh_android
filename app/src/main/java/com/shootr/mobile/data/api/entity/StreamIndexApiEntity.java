package com.shootr.mobile.data.api.entity;

import java.util.ArrayList;

public class StreamIndexApiEntity {

  private String idStream;
  private String streamTitle;
  private ArrayList<Integer> indices;

  public String getIdStream() {
    return idStream;
  }

  public void setIdStream(String idStream) {
    this.idStream = idStream;
  }

  public String getStreamTitle() {
    return streamTitle;
  }

  public void setStreamTitle(String streamTitle) {
    this.streamTitle = streamTitle;
  }

  public ArrayList<Integer> getIndices() {
    return indices;
  }

  public void setIndices(ArrayList<Integer> indices) {
    this.indices = indices;
  }
}
