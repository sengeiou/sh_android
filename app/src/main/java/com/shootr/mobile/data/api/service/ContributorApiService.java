package com.shootr.mobile.data.api.service;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.entity.ContributorEntity;
import java.io.IOException;
import java.util.List;
import retrofit.http.GET;
import retrofit.http.Query;

public interface ContributorApiService {

    @GET("/contributor?includeEmbed=false") List<ContributorEntity> getContributors(@Query("idStream") String idShot)
      throws ApiException, IOException;

    @GET("/contributor?includeEmbed=true") List<ContributorEntity> getContributorsWithUser(
      @Query("idStream") String idShot) throws ApiException, IOException;

    void addContributor(String idStream, String idUser) throws ApiException, IOException;
}
