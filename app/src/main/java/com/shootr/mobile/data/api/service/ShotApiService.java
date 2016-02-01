package com.shootr.mobile.data.api.service;

import com.shootr.mobile.data.api.entity.ShotApiEntity;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.entity.ShotEntity;
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

public interface ShotApiService {

    @GET("/shot/streamTimeline?includeLinks=false&includeAll=true")
    List<ShotApiEntity> getStreamTimeline(@Query("idStream") String stream, @Query("count") Integer count,
      @Query("sinceTimestamp") Long sinceTimestamp, @Query("maxTimestamp") Long maxTimestamp) throws ApiException, IOException;

    @GET("/shot/{idShot}/?includeLinks=false")
    ShotApiEntity getShot(@Path("idShot") String idShot) throws ApiException, IOException;

    @GET("/shot/{idShot}/?includeLinks=false&includeReplies=true")
    ShotApiEntity getShotWithReplies(@Path("idShot") String idShot) throws ApiException, IOException;

    @GET("/shot/{idShot}/?includeLinks=false&includeReplies=true&includeParent=true")
    ShotApiEntity getShotDetail(@Path("idShot") String idShot) throws ApiException, IOException;

    @GET("/shot/user/{idUser}/?includeLinks=false")
    List<ShotApiEntity> getShotsFromUser(@Path("idUser") String idUser,
      @Query("count") Integer limit,
      @Query("types") String[] types) throws ApiException, IOException;

    @GET("/shot/user/{idUser}/?includeLinks=false")
    List<ShotApiEntity> getAllShotsFromUser(@Path("idUser") String userId) throws ApiException, IOException;

    @GET("/shot/user/{idUser}/?includeLinks=false")
    List<ShotApiEntity> getAllShotsFromUserWithMaxDate(@Path("idUser") String userId,
      @Query("maxTimestamp") Long maxDate) throws ApiException, IOException;

    @PUT("/shot/{idShot}/nice")
    Response markNice(@Path("idShot") String idShot) throws ApiException, IOException;

    @DELETE("/shot/{idShot}/nice")
    Response unmarkNice(@Path("idShot") String idShot) throws ApiException, IOException;

    @GET("/shot/media/{idStream}/?count=100&includeLinks=false")
    List<ShotApiEntity> getMediaShots(@Path("idStream") String idStream, @Query("maxTimestamp") Long maxTimestamp) throws ApiException, IOException;

    @POST("/shot/{idShot}/share")
    Response shareShot(@Path("idShot") String idShot) throws ApiException, IOException;

    @DELETE("/shot/{idShot}")
    Response deleteShot(@Path("idShot") String idShot) throws ApiException, IOException;

    @POST("/shot") ShotEntity postNewShot(@Body ShotEntity shotEntity) throws ApiException, IOException;

    @GET("/shot/user/{idUser}/?includeLinks=false")
    List<ShotApiEntity> getAllShotsFromUserInStream(@Path("idUser") String userId,
      @Query("idStream") String idStream, @Query("sinceTimestamp") Long sinceTimestamp, @Query("maxTimestamp") Long maxTimestamp) throws ApiException, IOException;

    @PUT("/shot/{idShot}/hide")
    Response hideShot(@Path("idShot") String idShot) throws ApiException, IOException;

    @DELETE("/shot/{idShot}/hide")
    Response unhideShot(@Path("idShot") String idShot) throws ApiException, IOException;
}
