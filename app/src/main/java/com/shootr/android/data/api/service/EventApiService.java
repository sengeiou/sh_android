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

public interface EventApiService {

    @GET("/events/popular?includeLinks=false&includeEmbed=false")
    List<StreamEntity> getEventList(@Query("locale") String locale) throws IOException;

    @GET("/user/{idUser}/events?includeLinks=false&includeEmbed=false")
    List<StreamEntity> getEventListing(@Path("idUser") String idUserEvent,
      @Query("count") Integer maxNumberOfListingEvents) throws IOException;

    @GET("/events/search?includeLinks=false&includeEmbed=false") List<StreamEntity> getEvents(
      @Query("query") String query, @Query("locale") String locale) throws
      IOException;

    @DELETE("/events/{idEvent}")
    Response deleteEvent(@Path("idEvent") String idEvent) throws ApiException, IOException;
}
