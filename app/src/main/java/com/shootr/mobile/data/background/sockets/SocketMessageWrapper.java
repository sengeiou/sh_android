package com.shootr.mobile.data.background.sockets;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.shootr.mobile.data.api.entity.BaseMessageApiEntity;
import com.shootr.mobile.data.api.entity.PrintableItemApiEntity;
import com.shootr.mobile.data.api.entity.ShotApiEntity;
import com.shootr.mobile.data.api.entity.TopicApiEntity;
import com.shootr.mobile.data.api.entity.mapper.DataApiEntityMapper;
import com.shootr.mobile.data.api.entity.mapper.ShotApiEntityMapper;
import com.shootr.mobile.data.api.entity.mapper.TimelineApiEntityMapper;
import com.shootr.mobile.data.entity.FixedItemsSocketMessagesApiEntity;
import com.shootr.mobile.data.entity.PinnedItemsSocketMessageApiEntity;
import com.shootr.mobile.data.entity.SocketMessageApiEntity;
import com.shootr.mobile.data.entity.SocketMessageListener;
import com.shootr.mobile.data.entity.NewItemSocketMessageApiEntity;
import com.shootr.mobile.data.entity.TimelineMessageApiEntity;
import com.shootr.mobile.data.entity.DataEntity;
import com.shootr.mobile.data.entity.FollowableEntity;
import com.shootr.mobile.data.entity.PollEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.entity.TimelineEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.data.mapper.DataEntityMapper;
import com.shootr.mobile.data.mapper.ShotEntityMapper;
import com.shootr.mobile.data.mapper.TimelineEntityMapper;
import com.shootr.mobile.service.RuntimeTypeAdapterFactory;
import java.lang.reflect.Type;
import java.util.Date;
import javax.inject.Inject;

public class SocketMessageWrapper {

  private Gson gson;
  private SocketMessageListener messageListener;
  private final TimelineApiEntityMapper timelineApiEntityMapper;
  private final TimelineEntityMapper timelineEntityMapper;
  private final ShotApiEntityMapper shotApiEntityMapper;
  private final ShotEntityMapper shotEntityMapper;
  private final DataApiEntityMapper dataApiEntityMapper;
  private final DataEntityMapper dataEntityMapper;


  @Inject public SocketMessageWrapper(TimelineApiEntityMapper timelineApiEntityMapper,
      TimelineEntityMapper timelineEntityMapper, ShotApiEntityMapper shotApiEntityMapper,
      ShotEntityMapper shotEntityMapper, DataApiEntityMapper dataApiEntityMapper,
      DataEntityMapper dataEntityMapper) {
    this.timelineEntityMapper = timelineEntityMapper;
    this.shotApiEntityMapper = shotApiEntityMapper;
    this.shotEntityMapper = shotEntityMapper;
    this.dataApiEntityMapper = dataApiEntityMapper;
    this.dataEntityMapper = dataEntityMapper;
    this.messageListener = messageListener;
    this.timelineApiEntityMapper = timelineApiEntityMapper;
    gson = setupGson();
  }

  public void setMessageListener(SocketMessageListener messageListener) {
    this.messageListener = messageListener;
  }

  public void transformSocketMessage(String message) {
    try {
      SocketMessageApiEntity socketMessage = gson.fromJson(message, SocketMessageApiEntity.class);

      switch (socketMessage.getEventType()) {

        case SocketMessageApiEntity.TIMELINE:
          TimelineEntity timelineEntity =
              timelineApiEntityMapper.map(((TimelineMessageApiEntity) socketMessage).getData());
          messageListener.onTimeline(socketMessage.getRequestId(),
              timelineEntityMapper.map(timelineEntity));
          break;

        case SocketMessageApiEntity.NEW_ITEM_DATA:
          ShotEntity shotEntity = shotApiEntityMapper.transform(
              (ShotApiEntity) ((NewItemSocketMessageApiEntity) socketMessage).getData());
          messageListener.onNewItem(socketMessage.getRequestId(),
              shotEntityMapper.transform(shotEntity));
          break;

        case SocketMessageApiEntity.FIXED_ITEMS:
          DataEntity fixedItems =
              dataApiEntityMapper.map(((FixedItemsSocketMessagesApiEntity) socketMessage).getData());
          messageListener.onFixedItem(socketMessage.getRequestId(), fixedItems);
          break;
        case SocketMessageApiEntity.PINNED_ITEMS:
          DataEntity pinnedItems =
              dataApiEntityMapper.map(((PinnedItemsSocketMessageApiEntity) socketMessage).getData());
          messageListener.onPinnedItem(socketMessage.getRequestId(), pinnedItems);
          break;
        default:
          break;
      }
    } catch (Exception exception) {
      Log.d("socket", "error " + exception);
    }
  }

  private Gson setupGson() {

    final RuntimeTypeAdapterFactory<SocketMessageApiEntity> socketMessageRuntimeTypeAdapterFactory =
        RuntimeTypeAdapterFactory.of(SocketMessageApiEntity.class, "eventType")
            .registerSubtype(NewItemSocketMessageApiEntity.class, SocketMessageApiEntity.NEW_ITEM_DATA)
            .registerSubtype(TimelineMessageApiEntity.class, SocketMessageApiEntity.TIMELINE);

    final RuntimeTypeAdapterFactory<FollowableEntity> typeFactory =
        RuntimeTypeAdapterFactory.of(FollowableEntity.class, "resultType")
            .registerSubtype(StreamEntity.class, "STREAM")
            .registerSubtype(UserEntity.class, "USER");

    final RuntimeTypeAdapterFactory<PrintableItemApiEntity>
        printableItemApiEntityRuntimeTypeAdapterFactory =
        RuntimeTypeAdapterFactory.of(PrintableItemApiEntity.class, "resultType")
            .registerSubtype(ShotApiEntity.class, "SHOT")
            .registerSubtype(BaseMessageApiEntity.class, "BASE_MESSAGE")
            .registerSubtype(TopicApiEntity.class, "TOPIC")
            .registerSubtype(PollEntity.class, "POLL");

    return new GsonBuilder() //
        .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
          @Override public Date deserialize(JsonElement json, Type typeOfT,
              JsonDeserializationContext context) throws JsonParseException {
            return new Date(json.getAsJsonPrimitive().getAsLong());
          }
        })
        .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
          @Override
          public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getTime());
          }
        })
        .registerTypeAdapterFactory(typeFactory)
        .registerTypeAdapterFactory(printableItemApiEntityRuntimeTypeAdapterFactory)
        .registerTypeAdapterFactory(socketMessageRuntimeTypeAdapterFactory)
        .create();
  }
}
