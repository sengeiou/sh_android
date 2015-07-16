package com.shootr.android.data.api.service;

import com.shootr.android.data.api.entity.ChangeEmailApiEntity;
import com.shootr.android.data.api.entity.CreateAccountApiEntity;
import com.shootr.android.data.api.entity.FacebookLoginApiEntity;
import com.shootr.android.data.api.entity.LoginApiEntity;
import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.entity.UserEntity;
import com.squareup.okhttp.Response;
import java.io.IOException;
import retrofit.http.Body;
import retrofit.http.POST;

public interface AuthApiService {

    @POST("/auth/authenticate")
    UserEntity authenticate(@Body LoginApiEntity loginApiEntity) throws ApiException,  IOException;

    @POST("/auth/register")
    UserEntity createAccount(@Body CreateAccountApiEntity createAccountApiEntity) throws ApiException, IOException;

    @POST("/auth/socialAuthenticate/facebook")
    UserEntity authenticateWithFacebook(@Body FacebookLoginApiEntity facebookLoginApiEntity) throws ApiException, IOException;

    @POST("/auth/sendConfirmationEmail") Response confirmEmail() throws ApiException, IOException;

    @POST("/auth/changeEmail") Response changeEmail(@Body ChangeEmailApiEntity changeEmailApiEntity) throws ApiException, IOException;
}
