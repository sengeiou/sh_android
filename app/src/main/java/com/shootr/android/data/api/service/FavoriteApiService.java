package com.shootr.android.data.api.service;

import com.shootr.android.data.api.entity.FavoriteApiEntity;
import com.shootr.android.data.entity.FavoriteEntity;
import java.util.List;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface FavoriteApiService {

    @GET("/user/{idUser}/favorites/")
    List<FavoriteApiEntity> getFavorites(@Path("idUser") String idUser);

    @POST("/user/{idUser}/favorites/")
    FavoriteApiEntity createFavorite(@Path("idUser") String idUser, @Body FavoriteEntity favorite);


}
