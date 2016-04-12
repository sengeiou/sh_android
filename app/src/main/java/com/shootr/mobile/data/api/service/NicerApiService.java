package com.shootr.mobile.data.api.service;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.entity.NicerEntity;
import java.io.IOException;
import java.util.List;
import retrofit.http.GET;
import retrofit.http.Query;

public interface NicerApiService {

    @GET("/nice?includeEmbed=false") List<NicerEntity> getNicers(@Query("idShot") String idShot) throws ApiException, IOException;
}
