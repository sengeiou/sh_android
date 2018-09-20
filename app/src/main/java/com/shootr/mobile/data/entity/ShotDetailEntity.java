package com.shootr.mobile.data.entity;

public class ShotDetailEntity {

  private PrintableItemEntity item;
  private ItemsEntity parents;
  private StreamEntity stream;
  private RepliesEntity replies;

  public PrintableItemEntity getItem() {
    return item;
  }

  public void setItem(PrintableItemEntity item) {
    this.item = item;
  }

  public ItemsEntity getParents() {
    return parents;
  }

  public void setParents(ItemsEntity parents) {
    this.parents = parents;
  }

  public StreamEntity getStream() {
    return stream;
  }

  public void setStream(StreamEntity stream) {
    this.stream = stream;
  }

  public RepliesEntity getReplies() {
    return replies;
  }

  public void setReplies(RepliesEntity replies) {
    this.replies = replies;
  }
}
