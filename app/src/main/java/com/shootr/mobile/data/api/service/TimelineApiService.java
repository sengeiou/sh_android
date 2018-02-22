package com.shootr.mobile.data.api.service;

import com.shootr.mobile.data.api.entity.PrintableItemApiEntity;
import com.shootr.mobile.data.api.entity.TimelineApiEntity;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.entity.HeaderEntity;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.RestMethod;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public interface TimelineApiService {

  @GET("/stream/{idStream}/timeline/{timelineType}") TimelineApiEntity getTimeline(
      @Path("idStream") String idStream, @Path("timelineType") String timelineType,
      @Query("sinceTimestamp") Long timestamp) throws IOException, ApiException;

  @GET("/stream/{idStream}/timeline/{timelineType}") TimelineApiEntity getPaginatedTimeline(
      @Path("idStream") String idStream, @Path("timelineType") String timelineType,
      @Query("maxTimestamp") Long timestamp) throws IOException, ApiException;

  @PUT("/stream/{idStream}/header2") Response highlightItem(
      @Path("idStream") String idStream, @Body HeaderEntity highlightItemDataEntity)
      throws IOException, ApiException;

  @DELETEWITHBODY("/stream/{idStream}/header2") PrintableItemApiEntity deleteHighlightedItem(
      @Path("idStream") String idStream, @Body HeaderEntity highlightItemDataEntity)
      throws IOException, ApiException;

  @PUT("/stream/{idStream}/header1") Response createTopic(
      @Path("idStream") String idStream, @Body HeaderEntity highlightItemDataEntity)
      throws IOException, ApiException;

  @DELETEWITHBODY("/stream/{idStream}/header1") Response deleteTopic(
      @Path("idStream") String idStream, @Body HeaderEntity highlightItemDataEntity)
      throws IOException, ApiException;

  @Target(METHOD) @Retention(RUNTIME) @RestMethod(value = "DELETE", hasBody = true)
  @interface DELETEWITHBODY {
    String value();
  }

}
