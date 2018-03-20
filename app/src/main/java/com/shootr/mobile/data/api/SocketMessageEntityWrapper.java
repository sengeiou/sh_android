package com.shootr.mobile.data.api;

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
import com.shootr.mobile.data.api.entity.ExternalVideoApiEntity;
import com.shootr.mobile.data.api.entity.PrintableItemApiEntity;
import com.shootr.mobile.data.api.entity.ShotApiEntity;
import com.shootr.mobile.data.api.entity.TopicApiEntity;
import com.shootr.mobile.data.entity.AckSocketMessageApiEntity;
import com.shootr.mobile.data.entity.FixedItemsSocketMessagesApiEntity;
import com.shootr.mobile.data.entity.FollowableEntity;
import com.shootr.mobile.data.entity.NewBadgeContentSocketMessageApiEntity;
import com.shootr.mobile.data.entity.NewItemSocketMessageApiEntity;
import com.shootr.mobile.data.entity.ParticipantsSocketMessageApiEntity;
import com.shootr.mobile.data.entity.PinnedItemsSocketMessageApiEntity;
import com.shootr.mobile.data.entity.PollEntity;
import com.shootr.mobile.data.entity.SocketMessageApiEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.entity.TimelineMessageApiEntity;
import com.shootr.mobile.data.entity.UpdateItemSocketMessageApiEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.service.RuntimeTypeAdapterFactory;
import java.lang.reflect.Type;
import java.util.Date;
import javax.inject.Inject;

public class SocketMessageEntityWrapper {

  private Gson gson;

  @Inject public SocketMessageEntityWrapper() {
    gson = setupGson();
  }

  public SocketMessageApiEntity transformSocketMessage(String message) {
    try {
      return gson.fromJson(message, SocketMessageApiEntity.class);
    } catch (Exception exception) {
      Log.d("socket", "error " + exception);
    }
    return null;
  }

  public String transformEvent(SocketMessageApiEntity socketMessageEntity) {
    String event = gson.toJson(socketMessageEntity);
    Log.d("socket", "envio a sever: " + event);
    return event;
  }

  private Gson setupGson() {

    final RuntimeTypeAdapterFactory<SocketMessageApiEntity> socketMessageRuntimeTypeAdapterFactory =
        RuntimeTypeAdapterFactory.of(SocketMessageApiEntity.class, "type")
            .registerSubtype(NewItemSocketMessageApiEntity.class, SocketMessageApiEntity.NEW_ITEM_DATA)
            .registerSubtype(UpdateItemSocketMessageApiEntity.class, SocketMessageApiEntity.UPDATE_ITEM_DATA)
            .registerSubtype(TimelineMessageApiEntity.class, SocketMessageApiEntity.TIMELINE)
            .registerSubtype(ParticipantsSocketMessageApiEntity.class, SocketMessageApiEntity.PARTICIPANTS_UPDATE)
            .registerSubtype(PinnedItemsSocketMessageApiEntity.class, SocketMessageApiEntity.H1_ITEMS)
            .registerSubtype(FixedItemsSocketMessagesApiEntity.class, SocketMessageApiEntity.H2_ITEMS)
            .registerSubtype(NewBadgeContentSocketMessageApiEntity.class, SocketMessageApiEntity.NEW_BADGE_CONTENT)

            .registerSubtype(AckSocketMessageApiEntity.class, SocketMessageApiEntity.ACK);

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
            .registerSubtype(ExternalVideoApiEntity.class, "EXTERNAL_VIDEO")
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
