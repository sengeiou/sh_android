package com.shootr.mobile.data;

import com.shootr.mobile.domain.repository.SessionRepository;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;
import java.io.IOException;
import javax.inject.Inject;

public class SynchroTimeInterceptor implements Interceptor {

  private final SessionRepository sessionRepository;

  @Inject SynchroTimeInterceptor(SessionRepository sessionRepository) {
    this.sessionRepository = sessionRepository;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    Response response = chain.proceed(chain.request());
    Headers allHeaders = response.headers();
    String synchroTime = allHeaders.get("Sync");
    sessionRepository.setSynchroTime(synchroTime);
    return response;
  }
}
