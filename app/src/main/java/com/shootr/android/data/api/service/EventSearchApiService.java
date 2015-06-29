package com.shootr.android.data.api.service;

import com.shootr.android.data.entity.EventSearchEntity;
import java.io.IOException;
import java.util.List;
import retrofit.http.GET;
import retrofit.http.Query;

public interface EventSearchApiService {

    @GET("/events/search?includeLinks=false&includeEmbed=false") List<EventSearchEntity> getEvents(
      @Query("query") String query, @Query("locale") String locale) throws
      IOException;

}
