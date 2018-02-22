package com.shootr.mobile.data;

import com.shootr.mobile.domain.repository.SessionRepository;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import javax.inject.Inject;

class AuthHeaderInterceptor implements Interceptor {

  private final SessionRepository sessionRepository;

  @Inject AuthHeaderInterceptor(SessionRepository sessionRepository) {
    this.sessionRepository = sessionRepository;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    String sessionToken = sessionRepository.getSessionToken();
    if (sessionToken != null) {
      Request originalRequest = chain.request();
      Request authenticatedRequest =
          originalRequest.newBuilder().header("X-Auth-Token", sessionToken).build();
      return chain.proceed(authenticatedRequest);
    } else {
      return chain.proceed(chain.request());
    }
  }
}
