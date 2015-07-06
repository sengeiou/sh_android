package com.shootr.android.data.api.service;

import com.shootr.android.data.entity.ForgotPasswordEntity;
import com.shootr.android.data.entity.ForgotPasswordResultEntity;
import retrofit.http.Body;
import retrofit.http.POST;

public interface ResetPasswordApiService {

    @POST("/auth/forgotPassword") ForgotPasswordResultEntity passwordReset(@Body ForgotPasswordEntity forgotPasswordEntity);

    

}
