package com.shootr.mobile.domain.model;

import com.shootr.mobile.domain.model.stream.Stream;
import java.util.ArrayList;

public class UserStreams {

  private ArrayList<Stream> streams;

  public ArrayList<Stream> getStreams() {
    return streams;
  }

  public void setStreams(ArrayList<Stream> streams) {
    this.streams = streams;
  }

  @Override public String toString() {
    return "UserStreams{" + "streams=" + streams + '}';
  }
}
