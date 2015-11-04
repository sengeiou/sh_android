package com.shootr.mobile.data.api.service;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.entity.ChangePasswordEntity;
import java.io.IOException;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

public interface ChangePasswordApiService {

    @POST("/auth/changePassword") Response changePassword(@Body ChangePasswordEntity changePasswordEntity) throws ApiException,
      IOException;

}
