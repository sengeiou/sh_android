package com.shootr.android.data.api.service;

import com.shootr.android.data.api.entity.FavoriteApiEntity;
import com.shootr.android.data.entity.FavoriteEntity;
import java.io.IOException;
import java.util.List;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface FavoriteApiService {

    @GET("/user/favorites/")
    List<FavoriteApiEntity> getFavorites() throws IOException;

    @POST("/user/favorites/")
    FavoriteApiEntity createFavorite(@Body FavoriteEntity favorite) throws IOException;

    @DELETE("/user/favorites/stream/{idEvent}")
    Response deleteFavorite(@Path("idEvent") String idEvent) throws IOException;
}
