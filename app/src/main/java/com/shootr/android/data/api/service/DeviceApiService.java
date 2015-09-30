package com.shootr.android.data.api.service;

import com.shootr.android.data.entity.DeviceEntity;
import retrofit.http.Body;
import retrofit.http.POST;

public interface DeviceApiService {

    @POST("/device")
    DeviceEntity createUpdateDevice(@Body DeviceEntity device);
}
