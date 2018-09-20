package com.shootr.mobile.domain.model;

public class Bootstrapping {

  private Socket socket;
  private boolean socketConnection;
  private boolean superShot;
  private String logsUrl;

  public Bootstrapping() {
    this.socketConnection = false;
    this.superShot = false;
  }

  public Socket getSocket() {
    return socket;
  }

  public void setSocket(Socket socket) {
    this.socket = socket;
  }

  public boolean isSocketConnection() {
    return socketConnection;
  }

  public void setSocketConnection(boolean socketConnection) {
    this.socketConnection = socketConnection;
  }

  public String getLogsUrl() {
    return logsUrl;
  }

  public void setLogsUrl(String logsUrl) {
    this.logsUrl = logsUrl;
  }

  public boolean isSuperShot() {
    return superShot;
  }

  public void setSuperShot(boolean superShot) {
    this.superShot = superShot;
  }
}
