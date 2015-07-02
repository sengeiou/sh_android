package com.shootr.android.data.api.service;

import com.shootr.android.data.api.entity.CreateAccountApiEntity;
import com.shootr.android.data.api.entity.FacebookLoginApiEntity;
import com.shootr.android.data.api.entity.LoginApiEntity;
import com.shootr.android.data.entity.UserEntity;
import java.io.IOException;
import retrofit.http.Body;
import retrofit.http.POST;

public interface AuthApiService {

    @POST("/auth/authenticate")
    UserEntity authenticate(@Body LoginApiEntity loginApiEntity) throws IOException;

    @POST("/auth/register")
    UserEntity createAccount(@Body CreateAccountApiEntity createAccountApiEntity) throws IOException;

    @POST("/auth/socialAuthenticate/facebook")
    UserEntity authenticateWithFacebook(@Body FacebookLoginApiEntity facebookLoginApiEntity);
}
