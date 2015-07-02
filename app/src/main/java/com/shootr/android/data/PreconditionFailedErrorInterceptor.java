package com.shootr.android.data;

import android.app.Application;
import com.shootr.android.constant.Constants;
import com.shootr.android.data.bus.PreconditionFailed;
import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.util.VersionUtils;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import javax.inject.Inject;

public class PreconditionFailedErrorInterceptor implements Interceptor {

    public static final int CODE_PRECONDITION_FAILED = 412;

    private final long currentVersion;
    private final BusPublisher busPublisher;

    @Inject public PreconditionFailedErrorInterceptor(BusPublisher busPublisher, Application application) {
        this.busPublisher = busPublisher;
        currentVersion = VersionUtils.getVersionCode(application);;
    }

    @Override public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request preconditionRequest = originalRequest.newBuilder()
          .header("X-App-Platform", String.valueOf(Constants.ANDROID_PLATFORM))
          .addHeader("X-App-Version", String.valueOf(currentVersion)).build();
        Response response = chain.proceed(preconditionRequest);
        if (response.code() == CODE_PRECONDITION_FAILED) {
            busPublisher.post(new PreconditionFailed.Event());
        }
        return response;
    }
}
