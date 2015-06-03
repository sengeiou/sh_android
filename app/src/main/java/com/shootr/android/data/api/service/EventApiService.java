package com.shootr.android.data.api.service;

import com.shootr.android.data.entity.EventEntity;
import java.io.IOException;
import java.util.List;
import retrofit.http.GET;
import retrofit.http.Query;

public interface EventApiService {

    @GET("/events/popular")
    List<EventEntity> getEventList(@Query("me") String idUser, @Query("locale") String locale) throws IOException;
}
