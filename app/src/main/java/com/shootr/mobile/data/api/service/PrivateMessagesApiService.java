package com.shootr.mobile.data.api.service;

import com.shootr.mobile.data.api.entity.PrivateMessageTimelineEntity;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.entity.PrivateMessageChannelEntity;
import com.shootr.mobile.data.entity.PrivateMessageEntity;
import java.io.IOException;
import java.util.List;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface PrivateMessagesApiService {

  @GET("/privateMessage/timeline?embedPrivateMessageChannel=true")
  PrivateMessageTimelineEntity getTimeline(@Query("idTargetUser") String idTargetUser)
      throws ApiException, IOException;

  @POST("/privateMessage") PrivateMessageEntity putPrivateMesssage(
      @Body PrivateMessageEntity privateMessageEntity) throws ApiException, IOException;

  @GET("/privateMessageChannel") List<PrivateMessageChannelEntity> getPrivateMessageChannel(
      @Query("modifiedTimestamp") Long modifiedTimestamp) throws ApiException, IOException;

  @GET("/privateMessage/timeline?embedPrivateMessageChannel=false")
  PrivateMessageTimelineEntity refreshTimeline(@Query("idTargetUser") String idTargetUser,
      @Query("sinceTimestamp") Long timestamp) throws ApiException, IOException;

  @GET("/privateMessage/timeline?embedPrivateMessageChannel=false")
  PrivateMessageTimelineEntity getOlderTimeline(@Query("idTargetUser") String idTargetUser,
      @Query("maxTimestamp") Long timestamp) throws ApiException, IOException;
}
