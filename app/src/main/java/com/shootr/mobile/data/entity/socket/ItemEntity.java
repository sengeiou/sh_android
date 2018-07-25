package com.shootr.mobile.data.entity.socket;

import com.shootr.mobile.data.entity.PrintableItemEntity;

public class ItemEntity {

  private PrintableItemEntity item;
  private String list;

  public PrintableItemEntity getItem() {
    return item;
  }

  public void setItem(PrintableItemEntity item) {
    this.item = item;
  }

  public String getList() {
    return list;
  }

  public void setList(String list) {
    this.list = list;
  }
}
