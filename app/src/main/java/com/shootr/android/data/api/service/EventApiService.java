package com.shootr.android.data.api.service;

import com.shootr.android.data.entity.EventEntity;
import java.io.IOException;
import java.util.List;
import retrofit.http.GET;
import retrofit.http.Query;

public interface EventApiService {

    @GET("/events/popular?includeLinks=false&includeEmbed=false")
    List<EventEntity> getEventList(@Query("locale") String locale) throws IOException;

    @GET("/events/popular?includeLinks=false&includeEmbed=false")
    List<EventEntity> getEventListing(@Query("idUser") String idUserEvent,
      @Query("locale") String locale, @Query("count") Integer maxNumberOfListingEvents) throws IOException;

    @GET("/events/search?includeLinks=false&includeEmbed=false") List<EventEntity> getEvents(
      @Query("query") String query, @Query("locale") String locale) throws
      IOException;
}
