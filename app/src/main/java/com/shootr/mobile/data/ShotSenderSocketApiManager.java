package com.shootr.mobile.data;

import com.shootr.mobile.data.api.SendSocketEventListener;
import com.shootr.mobile.data.entity.socket.CreateShotSocketMessageApiService;
import com.shootr.mobile.data.entity.socket.CreatedShotSocketMessageApiEntity;
import com.shootr.mobile.data.entity.EventParams;
import com.shootr.mobile.data.entity.socket.NewShotContentApiEntity;
import com.shootr.mobile.data.entity.NewShotParameters;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.data.entity.socket.SocketMessageApiEntity;
import java.util.HashMap;
import java.util.UUID;

public class ShotSenderSocketApiManager {

  private final int VERSION = 1;
  private final SendSocketEventListener sendSocketEventListener;

  private HashMap<String, String> sentShots;

  public ShotSenderSocketApiManager(SendSocketEventListener sendSocketEventListener) {
    this.sendSocketEventListener = sendSocketEventListener;
    sentShots = new HashMap<>();
  }

  public void sendNewShot(ShotEntity shotEntity, String idQueue) {
    CreateShotSocketMessageApiService createShotSocketMessageApiService = new CreateShotSocketMessageApiService();
    NewShotContentApiEntity newShotContentApiEntity = new NewShotContentApiEntity();
    NewShotParameters newShotParameters = new NewShotParameters();
    String requestId = generateRequestId();

    newShotParameters.setComment(shotEntity.getComment());
    newShotParameters.setIdStream(shotEntity.getIdStream());
    newShotParameters.setType(shotEntity.getType());
    newShotParameters.setImageMedia(shotEntity.getImageIdMedia());
    newShotParameters.setIdShotParent(shotEntity.getIdShotParent());

    newShotContentApiEntity.setShot(newShotParameters);
    createShotSocketMessageApiService.setData(newShotContentApiEntity);

    createShotSocketMessageApiService.setRequestId(requestId);
    createShotSocketMessageApiService.setVersion(VERSION);

    sentShots.put(requestId, idQueue);
    sendSocketEventListener.sendEvent(createShotSocketMessageApiService);
  }

  public SocketMessageApiEntity setupShotSent(SocketMessageApiEntity socketMessage) {
    if (sentShots.containsKey(socketMessage.getRequestId())) {
      ((CreatedShotSocketMessageApiEntity) socketMessage).setIdQueue(sentShots.get(socketMessage.getRequestId()));
    }
    return socketMessage;
  }

  public SocketMessageApiEntity setupIdQueue(SocketMessageApiEntity socketMessage) {
    EventParams eventParams = new EventParams();
    if (sentShots.containsKey(socketMessage.getRequestId())) {
      eventParams.setIdShot(sentShots.get(socketMessage.getRequestId()));
    }
    socketMessage.setEventParams(eventParams);
    return socketMessage;
  }

  private String generateRequestId() {
    return UUID.randomUUID().toString();
  }
}
