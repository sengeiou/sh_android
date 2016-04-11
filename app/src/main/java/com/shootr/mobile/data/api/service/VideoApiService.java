package com.shootr.mobile.data.api.service;

import com.shootr.mobile.data.entity.VideoEmbedEntity;
import com.shootr.mobile.domain.Shot;
import java.io.IOException;
import retrofit.http.Body;
import retrofit.http.POST;

public interface VideoApiService {

    @POST("/media/video/embed") VideoEmbedEntity postEmbdedVideo(@Body Shot shot) throws IOException;
}
