package com.shootr.android.data.api.service;

import com.shootr.android.data.entity.VideoEmbedEntity;
import com.shootr.android.domain.Shot;
import java.io.IOException;
import retrofit.http.Body;
import retrofit.http.POST;

public interface VideoApiService {

    @POST("/media/video/embed")
    VideoEmbedEntity postEmbdedVideo(@Body Shot shot) throws IOException;
}
