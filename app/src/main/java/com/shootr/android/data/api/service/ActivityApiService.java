package com.shootr.android.data.api.service;

import com.shootr.android.data.api.entity.ActivityApiEntity;
import java.io.IOException;
import java.util.List;
import retrofit.http.GET;
import retrofit.http.Query;

public interface ActivityApiService {

    @GET("/activity?includeFollowing=true&includeLinks=false") List<ActivityApiEntity> getActivityTimeline(@Query("me") String idUser,
      @Query("types") List<String> types,
      @Query("count") Integer count) throws IOException;

}
