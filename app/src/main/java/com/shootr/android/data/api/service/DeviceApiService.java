package com.shootr.android.data.api.service;

import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.entity.DeviceEntity;
import java.io.IOException;
import retrofit.http.Body;
import retrofit.http.POST;

public interface DeviceApiService {

    @POST("/device")
    DeviceEntity createUpdateDevice(@Body DeviceEntity device) throws IOException, ApiException;
}
