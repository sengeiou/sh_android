package com.shootr.android.data.api.service;

import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.entity.UserEntity;
import java.io.IOException;
import java.util.List;
import retrofit.http.GET;
import retrofit.http.Path;

public interface UserApiService {

    @GET("/user/{idUser}/following")
    List<UserEntity> getFollowing(@Path("idUser") String idUser) throws IOException, ApiException;

    @GET("/user/{idUser}/followers")
    List<UserEntity> getFollowers(@Path("idUser") String idUser) throws IOException, ApiException;

    @GET("/streams/{idStream}/participants")
    List<UserEntity> getAllParticipants(@Path("idStream") String idStream) throws IOException, ApiException;
}
