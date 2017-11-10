package com.shootr.mobile.data.api.service;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.entity.OnBoardingEntity;
import com.shootr.mobile.data.entity.OnBoardingFavoritesEntity;
import java.io.IOException;
import java.util.List;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface OnBoardingApiService {

    @GET("/onboarding") List<OnBoardingEntity> getFavoritesOnboarding(@Query("type") String type,
        @Query("locale") String locale) throws ApiException, IOException;

    @POST("/followable/bulk") List<OnBoardingFavoritesEntity> addOnBoardingFavorites(
        @Body OnBoardingFavoritesEntity onBoardingFavoritesEntity) throws ApiException, IOException;

}
