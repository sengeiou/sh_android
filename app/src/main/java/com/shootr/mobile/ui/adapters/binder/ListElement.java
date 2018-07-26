package com.shootr.mobile.ui.adapters.binder;

public class ListElement {

  public static final String SEPARATOR = "separator";
  public static final String HEADER = "header";
  public static final String NON_SUBSCRIBERS = "non_subscribers";

  private String elementType;

  public ListElement(String elementType) {
    this.elementType = elementType;
  }

  public String getElementType() {
    return elementType;
  }

  public void setElementType(String elementType) {
    this.elementType = elementType;
  }
}
