package com.shootr.android.data.api.service;

import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.entity.StreamEntity;
import java.io.IOException;
import java.util.List;
import retrofit.client.Response;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface StreamApiService {

    @GET("/events/popular?includeLinks=false&includeEmbed=false")
    List<StreamEntity> getStreamList(@Query("locale") String locale) throws IOException;

    @GET("/user/{idUser}/events?includeLinks=false&includeEmbed=false")
    List<StreamEntity> getStreamListing(@Path("idUser") String idUserEvent,
      @Query("count") Integer maxNumberOfListingEvents) throws IOException;

    @GET("/events/search?includeLinks=false&includeEmbed=false") List<StreamEntity> getStreams(
      @Query("query") String query, @Query("locale") String locale) throws
      IOException;

    @DELETE("/events/{idEvent}")
    Response deleteStream(@Path("idEvent") String idEvent) throws ApiException, IOException;
}
