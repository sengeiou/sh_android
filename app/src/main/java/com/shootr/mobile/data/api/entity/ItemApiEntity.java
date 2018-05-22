package com.shootr.mobile.data.api.entity;

public class ItemApiEntity {

  private PrintableItemApiEntity item;
  private String list;

  public PrintableItemApiEntity getItem() {
    return item;
  }

  public void setItem(PrintableItemApiEntity item) {
    this.item = item;
  }

  public String getList() {
    return list;
  }

  public void setList(String list) {
    this.list = list;
  }
}
