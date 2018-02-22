package com.shootr.mobile.data.background.sockets;

import android.util.Log;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class SocketListener extends WebSocketListener {

  private WebSocketConnection webSocketConnection;

  public SocketListener(WebSocketConnection webSocketConnection) {
    this.webSocketConnection = webSocketConnection;
  }

  @Override public void onOpen(WebSocket webSocket, Response response) {
    log("Abro conexión");
    webSocketConnection.onConnected();
  }

  @Override public void onMessage(WebSocket webSocket, String text) {
    log("Recibo un menssage en String " + text);
    webSocketConnection.onMessage(text);
  }

  @Override public void onMessage(WebSocket webSocket, ByteString bytes) {
    log("Recibo un menssage en ByteString");
  }

  @Override public void onClosing(WebSocket webSocket, int code, String reason) {
    log("cerrando socket " + reason);
  }

  @Override public void onClosed(WebSocket webSocket, int code, String reason) {
    log("SocketEntity cerrado");
    webSocketConnection.onClosed();
  }

  @Override public void onFailure(WebSocket webSocket, Throwable t, Response response) {
    log("He tenido un fallo " + t);
    log("Response del fallo: " + response);
    webSocketConnection.onFailure(t);

  }

  private void log(String message) {
    Log.d("SOCKET", message);
  }


  public interface WebSocketConnection {
    void onFailure(Throwable t);

    void onMessage(String message);

    void onConnected();

    void onClosed();
  }
}
