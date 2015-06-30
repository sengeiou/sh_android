package com.shootr.android.data.api.service;

import com.shootr.android.data.entity.UserEntity;
import java.io.IOException;
import retrofit.http.GET;
import retrofit.http.Query;

public interface AuthApiService {

    @GET("/auth/authenticate")
    UserEntity loginWithUsername(@Query("userName") String username, @Query("password") String password) throws IOException;

    @GET("/auth/authenticate")
    UserEntity loginWithEmail(@Query("email") String email, @Query("password") String password) throws IOException;
}
