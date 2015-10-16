package com.shootr.android.data.api.service;

import com.shootr.android.data.api.entity.FavoriteApiEntity;
import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.entity.FavoriteEntity;
import java.io.IOException;
import java.util.List;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface FavoriteApiService {

    @GET("/favorite")
    List<FavoriteApiEntity> getFavorites(@Query("idUser") String userId) throws ApiException, IOException;

    @POST("/favorite")
    FavoriteApiEntity createFavorite(@Body FavoriteEntity favorite) throws IOException, ApiException;

    @DELETE("/favorite/{idFavoriteOrStream}")
    Response deleteFavorite(@Path("idFavoriteOrStream") String idStream) throws ApiException, IOException;
}
