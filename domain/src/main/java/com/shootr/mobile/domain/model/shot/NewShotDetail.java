package com.shootr.mobile.domain.model.shot;

import com.shootr.mobile.domain.model.PrintableItem;
import com.shootr.mobile.domain.model.TimelineItem;
import com.shootr.mobile.domain.model.stream.Stream;

public class NewShotDetail {

  private PrintableItem shot;
  private TimelineItem parents;
  private Stream stream;
  private Replies replies;

  public PrintableItem getShot() {
    return shot;
  }

  public void setShot(PrintableItem shot) {
    this.shot = shot;
  }

  public TimelineItem getParents() {
    return parents;
  }

  public void setParents(TimelineItem parents) {
    this.parents = parents;
  }

  public Stream getStream() {
    return stream;
  }

  public void setStream(Stream stream) {
    this.stream = stream;
  }

  public Replies getReplies() {
    return replies;
  }

  public void setReplies(Replies replies) {
    this.replies = replies;
  }
}
