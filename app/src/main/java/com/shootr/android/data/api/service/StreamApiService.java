package com.shootr.android.data.api.service;

import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.entity.StreamEntity;
import java.io.IOException;
import java.util.List;
import retrofit.client.Response;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface StreamApiService {

    @GET("/streams/{idStream}?includeLinks=false&includeMediaCountByRelatedUsers=true")
    StreamEntity getStream(@Path("idStream") String idStream) throws IOException, ApiException;

    @GET("/streams/popular?includeLinks=false&includeEmbed=false")
    List<StreamEntity> getStreamList(@Query("locale") String locale) throws IOException;

    @GET("/user/{idUser}/streams?includeLinks=false&includeEmbed=false")
    List<StreamEntity> getStreamListing(@Path("idUser") String idUserStream,
      @Query("count") Integer maxNumberOfListingStreams) throws IOException;

    @GET("/streams/search?includeLinks=false&includeEmbed=false") List<StreamEntity> getStreams(
      @Query("query") String query, @Query("locale") String locale) throws
      IOException;

    @DELETE("/streams/{idStream}")
    Response deleteStream(@Path("idStream") String idStream) throws ApiException, IOException;

    @POST("/streams/{idStream}/recommend")
    Response recommendStream(@Path("idStream") String idStream) throws IOException;
}
