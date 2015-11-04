package com.shootr.mobile.data;

import android.app.Application;
import com.shootr.mobile.constant.Constants;
import com.shootr.mobile.util.VersionUtils;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import javax.inject.Inject;

public class VersionHeaderInterceptor implements Interceptor {

    private final long currentVersion;

    @Inject public VersionHeaderInterceptor(Application application) {
        currentVersion = VersionUtils.getVersionCode(application);
    }

    @Override public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request versionRequest = originalRequest.newBuilder()
          .header("X-App-Platform", String.valueOf(Constants.ANDROID_PLATFORM))
          .addHeader("X-App-Version", String.valueOf(currentVersion)).build();
        return chain.proceed(versionRequest);
    }
}
