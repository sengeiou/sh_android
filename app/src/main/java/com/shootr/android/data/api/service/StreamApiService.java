package com.shootr.android.data.api.service;

import com.shootr.android.data.api.entity.WatchersApiEntity;
import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.entity.StreamEntity;
import java.io.IOException;
import java.util.List;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface StreamApiService {

    @POST("/streams")
    StreamEntity createStream(@Body StreamEntity streamEntity) throws IOException, ApiException;

    @PUT("/streams")
    StreamEntity updateStream(@Body StreamEntity streamEntity) throws IOException, ApiException;

    @GET("/streams/{idStream}?watchersCount=51&includeWatchers=true&includeLinks=false&includeMediaCountByRelatedUsers=true")
    StreamEntity getStream(@Path("idStream") String idStream) throws IOException, ApiException;

    @GET("/streams?includeLinks=false")
    List<StreamEntity> getStreams(@Query("idStreams") List<String> idStreams) throws IOException, ApiException;

    @GET("/streams/popular?includeLinks=false&includeEmbed=false")
    List<StreamEntity> getStreamList(@Query("locale") String locale) throws ApiException, IOException;

    @GET("/user/{idUser}/streams?includeLinks=false&includeEmbed=false")
    List<StreamEntity> getStreamListing(@Path("idUser") String idUserStream,
      @Query("count") Integer maxNumberOfListingStreams) throws ApiException, IOException;

    @GET("/streams/search?includeLinks=false&includeEmbed=false") List<StreamEntity> getStreams(
      @Query("query") String query, @Query("locale") String locale) throws
      ApiException, IOException;

    @GET("/user/{idUser}/listedCount")
    Integer getListingCount(@Path("idUser")String idUser) throws ApiException, IOException;

    @POST("/streams/{idStream}/share")
    Response shareStream(@Path("idStream") String idStream) throws ApiException, IOException;

    @GET("/streams/watchers/") List<WatchersApiEntity> getHolderWatchers() throws IOException, ApiException;

    @PUT("/streams/{idStream}/checkin")
    Response checkIn(@Path("idStream") String idStream) throws IOException, ApiException;

    @PUT("/streams/{idStream}/remove")
    Response removeStream(@Path("idStream") String idStream) throws IOException, ApiException;

    @PUT("/streams/{idStream}/restore")
    Response restoreStream(@Path("idStream") String idStream) throws IOException, ApiException;
}
