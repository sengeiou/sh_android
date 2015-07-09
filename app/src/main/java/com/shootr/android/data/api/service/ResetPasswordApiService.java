package com.shootr.android.data.api.service;

import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.entity.ForgotPasswordEntity;
import com.shootr.android.data.entity.ForgotPasswordResultEntity;
import com.shootr.android.data.entity.ResetPasswordEntity;
import java.io.IOException;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

public interface ResetPasswordApiService {

    @POST("/auth/forgotPassword") ForgotPasswordResultEntity passwordReset(@Body ForgotPasswordEntity forgotPasswordEntity) throws ApiException, IOException;

    @POST("/auth/resetPassword") Response sendResetPasswordEmail(@Body ResetPasswordEntity resetPasswordEntity) throws IOException;


}
