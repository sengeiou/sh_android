package com.shootr.mobile.data;

import com.shootr.mobile.domain.repository.SessionRepository;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import javax.inject.Inject;

public class DeviceHeaderInterceptor implements Interceptor {

  private final SessionRepository sessionRepository;

  @Inject DeviceHeaderInterceptor(SessionRepository sessionRepository) {
    this.sessionRepository = sessionRepository;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    String deviceId = sessionRepository.getDeviceId();
    if (deviceId != null) {
      Request originalRequest = chain.request();
      Request authenticatedRequest = originalRequest.newBuilder().header("Device", deviceId).build();
      return chain.proceed(authenticatedRequest);
    } else {
      return chain.proceed(chain.request());
    }
  }
}
