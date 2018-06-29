package com.shootr.mobile.data.entity;

public class ErrorEntity {

  private String command;
  private int errorCode;

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public int getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }
}
