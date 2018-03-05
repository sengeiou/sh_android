package com.shootr.mobile.data.api;

import android.util.Log;
import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;
import com.shootr.mobile.data.background.sockets.SocketListener;
import java.util.List;
import java.util.Map;

public class WebSocketListenerImpl implements WebSocketListener {

  private SocketListener.WebSocketConnection webSocketConnection;

  public WebSocketListenerImpl(SocketListener.WebSocketConnection webSocketConnection) {
    this.webSocketConnection = webSocketConnection;
  }

  @Override public void onStateChanged(WebSocket websocket, WebSocketState newState)
      throws Exception {
    if (newState.equals(WebSocketState.CLOSED)) {
      log("SocketEntity cerrado");
      webSocketConnection.onClosed();
    }
  }

  @Override public void onConnected(WebSocket websocket, Map<String, List<String>> headers)
      throws Exception {
    log("Abro conexión");
    webSocketConnection.onConnected();

  }

  @Override public void onConnectError(WebSocket websocket, WebSocketException cause)
      throws Exception {
    log("He tenido un fallo " + cause);
    log("Response del fallo: " + cause);
    webSocketConnection.onFailure(cause);
  }

  @Override public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame,
      WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {

  }

  @Override public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

  }

  @Override public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame)
      throws Exception {

  }

  @Override public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

  }

  @Override public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

  }

  @Override public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

  }

  @Override public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

  }

  @Override public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

  }

  @Override public void onTextMessage(WebSocket websocket, String text) throws Exception {
    log("Recibo un menssage en String " + text);
    webSocketConnection.onMessage(text);
  }

  @Override public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {

  }

  @Override public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

  }

  @Override public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {

  }

  @Override public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception {

  }

  @Override public void onThreadCreated(WebSocket websocket, ThreadType threadType, Thread thread)
      throws Exception {

  }

  @Override public void onThreadStarted(WebSocket websocket, ThreadType threadType, Thread thread)
      throws Exception {

  }

  @Override public void onThreadStopping(WebSocket websocket, ThreadType threadType, Thread thread)
      throws Exception {

  }

  @Override public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
    log("He tenido un fallo " + cause);
    log("Response del fallo: " + cause);
    webSocketConnection.onFailure(cause);
  }

  @Override
  public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame)
      throws Exception {

  }

  @Override public void onMessageError(WebSocket websocket, WebSocketException cause,
      List<WebSocketFrame> frames) throws Exception {

  }

  @Override public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause,
      byte[] compressed) throws Exception {

  }

  @Override
  public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data)
      throws Exception {

  }

  @Override
  public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame)
      throws Exception {

  }

  @Override public void onUnexpectedError(WebSocket websocket, WebSocketException cause)
      throws Exception {

  }

  @Override public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {

  }

  @Override
  public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers)
      throws Exception {

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
