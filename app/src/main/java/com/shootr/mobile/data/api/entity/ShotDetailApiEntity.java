package com.shootr.mobile.data.api.entity;

import com.shootr.mobile.data.entity.StreamEntity;

public class ShotDetailApiEntity {

  private PrintableItemApiEntity item;
  private ItemsApiEntity parents;
  private StreamEntity stream;
  private RepliesApiEntity replies;

  public PrintableItemApiEntity getItem() {
    return item;
  }

  public void setItem(PrintableItemApiEntity item) {
    this.item = item;
  }

  public StreamEntity getStream() {
    return stream;
  }

  public void setStream(StreamEntity stream) {
    this.stream = stream;
  }

  public RepliesApiEntity getReplies() {
    return replies;
  }

  public void setReplies(RepliesApiEntity replies) {
    this.replies = replies;
  }

  public ItemsApiEntity getParents() {
    return parents;
  }

  public void setParents(ItemsApiEntity parents) {
    this.parents = parents;
  }
}
