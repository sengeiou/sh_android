package com.shootr.mobile.data.api.service;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.entity.ContributorEntity;
import java.io.IOException;
import java.util.List;
import retrofit.client.Response;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface ContributorApiService {

    @GET("/contributor?includeEmbed=false") List<ContributorEntity> getContributors(@Query("idStream") String idStream)
      throws ApiException, IOException;

    @GET("/contributor?includeEmbed=true") List<ContributorEntity> getContributorsWithUser(
      @Query("idStream") String idStream) throws ApiException, IOException;

    @PUT("/stream/{idStream}/contributor/{idUser}") Response addContributor(@Path("idStream") String idStream,
      @Path("idUser") String idUser) throws ApiException, IOException;

    @DELETE("/stream/{idStream}/contributor/{idUser}") Response removeContributor(@Path("idStream") String idStream,
      @Path("idUser") String idUser) throws ApiException, IOException;
}
