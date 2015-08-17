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

}
