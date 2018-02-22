package com.shootr.mobile.domain.model;

public class Bootstrapping {

  private Socket socket;
  private boolean timelineConnection;
  private boolean socketConnection;
  private String logsUrl;

  public Bootstrapping() {
    this.timelineConnection = false;
    this.socketConnection = false;
  }

  public Socket getSocket() {
    return socket;
  }

  public void setSocket(Socket socket) {
    this.socket = socket;
  }

  public boolean isTimelineConnection() {
    return timelineConnection;
  }

  public void setTimelineConnection(boolean timeline) {
    this.timelineConnection = timeline;
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
}
