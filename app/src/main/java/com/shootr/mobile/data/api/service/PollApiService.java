package com.shootr.mobile.data.api.service;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.entity.PollEntity;
import java.io.IOException;
import java.util.List;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface PollApiService {

  @GET("/poll") List<PollEntity> getPollByIdStream(
      @Query("idStream") String idStream) throws ApiException, IOException;

  @GET("/poll/{idPoll}") PollEntity getPollById(@Path("idPoll") String idPoll)
      throws ApiException, IOException;

  @POST("/poll/{idPoll}/vote/{idPollOption}") PollEntity vote(@Path("idPoll") String idPoll,
      @Path("idPollOption") String idPollOption) throws ApiException, IOException;
}
