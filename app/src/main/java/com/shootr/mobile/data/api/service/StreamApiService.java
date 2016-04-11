package com.shootr.mobile.data.api.service;

import com.shootr.mobile.data.api.entity.FavoritesApiEntity;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.entity.MuteStreamEntity;
import com.shootr.mobile.data.entity.StreamEntity;
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

    @POST("/stream") StreamEntity createStream(@Body StreamEntity streamEntity) throws IOException, ApiException;

    @PUT("/stream/") StreamEntity updateStream(@Body StreamEntity streamEntity,
      @Query("notifyPinMessage") Boolean notify) throws IOException, ApiException;

    @GET("/stream/{idStream}?watchersCount=51&includeWatchers=true"
      + "&includeLinks=false&includeMediaCountByRelatedUsers=true")
    StreamEntity getStream(@Path("idStream") String idStream) throws IOException, ApiException;

    @GET("/stream?includeLinks=false") List<StreamEntity> getStreams(@Query("idStreams") List<String> idStreams)
      throws IOException, ApiException;

    @GET("/stream/popular?includeLinks=false&includeEmbed=false") List<StreamEntity> getStreamList(
      @Query("locale") String locale) throws ApiException, IOException;

    @GET("/stream/user/{idUser}?includeLinks=false&includeEmbed=false") List<StreamEntity> getStreamListing(
      @Path("idUser") String idUserStream, @Query("count") Integer maxNumberOfListingStreams)
      throws ApiException, IOException;

    @GET("/stream/search?includeLinks=false&includeEmbed=false") List<StreamEntity> getStreams(
      @Query("query") String query, @Query("locale") String locale) throws ApiException, IOException;

    @POST("/stream/{idStream}/share") Response shareStream(@Path("idStream") String idStream)
      throws ApiException, IOException;

    @GET("/stream/favoriteCount/") List<FavoritesApiEntity> getHolderFavorites(@Query("idUser") String idUser)
      throws IOException, ApiException;

    @PUT("/stream/{idStream}/remove") Response removeStream(@Path("idStream") String idStream)
      throws IOException, ApiException;

    @PUT("/stream/{idStream}/restore") Response restoreStream(@Path("idStream") String idStream)
      throws IOException, ApiException;

    @GET("/stream/blog/") StreamEntity getBlogStream(@Query("country") String country,
      @Query("language") String language) throws IOException, ApiException;

    @GET("/stream/help/") StreamEntity getHelpStream(@Query("country") String country,
      @Query("language") String language) throws IOException, ApiException;

    @GET("/mute/") List<MuteStreamEntity> getMuteStreams() throws IOException, ApiException;

    @POST("/mute/{idStream}") Response mute(@Path("idStream") String idStream) throws ApiException, IOException;

    @DELETE("/mute/{idStream}") Response unmute(@Path("idStream") String idStream) throws IOException, ApiException;
}
