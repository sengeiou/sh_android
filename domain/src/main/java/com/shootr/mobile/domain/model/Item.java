package com.shootr.mobile.domain.model;

public class Item {

  private PrintableItem item;
  private String list;

  public PrintableItem getItem() {
    return item;
  }

  public void setItem(PrintableItem item) {
    this.item = item;
  }

  public String getList() {
    return list;
  }

  public void setList(String list) {
    this.list = list;
  }

  @Override public String toString() {
    return "Item{" + "item=" + item + ", list='" + list + '\'' + '}';
  }
}
