package com.shootr.mobile.data.api.service;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.entity.LandingStreamsEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.domain.model.stream.StreamUpdateParameters;
import java.io.IOException;
import java.util.List;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface StreamApiService {

  @GET("/stream/{idStream}?watchersCount=51&includeWatchers=true" + "&includeLinks=false")
  StreamEntity getStream(@Path("idStream") String idStream,
      @Query("streamReadWriteMode") String[] types) throws IOException, ApiException;

  @GET("/stream?includeLinks=false") List<StreamEntity> getStreams(
      @Query("idStreams") List<String> idStreams, @Query("streamReadWriteMode") String[] types)
      throws IOException, ApiException;

  @GET("/stream/popular?includeLinks=false&includeEmbed=false") List<StreamEntity> getStreamList(
      @Query("locale") String locale, @Query("streamReadWriteMode") String[] types)
      throws ApiException, IOException;

  @GET("/stream/user/{idUser}?includeLinks=false&includeEmbed=false")
  List<StreamEntity> getStreamListing(@Path("idUser") String idUserStream,
      @Query("count") Integer maxNumberOfListingStreams,
      @Query("streamReadWriteMode") String[] types) throws ApiException, IOException;

  @GET("/stream/search?includeLinks=false&includeEmbed=false") List<StreamEntity> getStreams(
      @Query("query") String query, @Query("locale") String locale,
      @Query("streamReadWriteMode") String[] types) throws ApiException, IOException;

  @GET("/stream/blog/") StreamEntity getBlogStream(@Query("country") String country,
      @Query("language") String language) throws IOException, ApiException;

  @GET("/stream/help/") StreamEntity getHelpStream(@Query("country") String country,
      @Query("language") String language) throws IOException, ApiException;

  @PUT("/stream/{idStream}/remove") Response removeStream(@Path("idStream") String idStream)
      throws IOException, ApiException;

  @PUT("/stream/{idStream}/restore") Response restoreStream(@Path("idStream") String idStream)
      throws IOException, ApiException;

  @POST("/stream/{idStream}/share") Response shareStream(@Path("idStream") String idStream)
      throws ApiException, IOException;

  @POST("/stream") StreamEntity createStream(@Body StreamEntity streamEntity)
      throws IOException, ApiException;

  @PUT("/stream/") StreamEntity updateStream(@Body StreamUpdateParameters streamUpdateParameters)
      throws IOException, ApiException;

  @POST("/stream/{idStream}/mute") Response mute(@Path("idStream") String idStream)
      throws IOException, ApiException;

  @DELETE("/stream/{idStream}/mute") Response unMute(@Path("idStream") String idStream)
      throws IOException, ApiException;

  @PUT("/stream/{idStream}/hide") Response hide(@Path("idStream") String idStream)
      throws IOException, ApiException;

  @POST("/stream/{idStream}/follow") Response followStream(@Path("idStream") String idStream)
      throws IOException, ApiException;

  @DELETE("/stream/{idStream}/follow") Response unFollowStream(@Path("idStream") String idStream)
      throws IOException, ApiException;

  @GET("/stream/landing") LandingStreamsEntity getLandingStreams() throws IOException, ApiException;

}
