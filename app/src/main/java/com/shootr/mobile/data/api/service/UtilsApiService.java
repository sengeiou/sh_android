package com.shootr.mobile.data.api.service;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.entity.socket.BootstrapingEntity;
import java.io.IOException;
import retrofit.http.GET;

public interface UtilsApiService {

  @GET("/bootstrapping") BootstrapingEntity getBootSocket() throws IOException, ApiException;
}
