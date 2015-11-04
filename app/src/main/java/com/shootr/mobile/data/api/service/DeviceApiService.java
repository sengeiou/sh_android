package com.shootr.mobile.data.api.service;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.entity.DeviceEntity;
import java.io.IOException;
import retrofit.http.Body;
import retrofit.http.POST;

public interface DeviceApiService {

    @POST("/device")
    DeviceEntity createUpdateDevice(@Body DeviceEntity device) throws IOException, ApiException;
}
