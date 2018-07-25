package com.shootr.mobile.data;

import com.shootr.mobile.domain.utils.LocaleProvider;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import javax.inject.Inject;

public class LanguageHeaderInterceptor implements Interceptor {

    private final LocaleProvider localeProvider;

    @Inject public LanguageHeaderInterceptor(LocaleProvider localeProvider) {
        this.localeProvider = localeProvider;
    }

    @Override public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request versionRequest = originalRequest.newBuilder()
          .header("Accept-Language", localeProvider.getLocaleInLanguageTag())
          .build();
        return chain.proceed(versionRequest);
    }
}
