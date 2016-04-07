package com.shootr.mobile.data;

import com.shootr.mobile.util.VersionUpdater;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;

import java.io.IOException;

import javax.inject.Inject;

public class VersionOutdatedErrorInterceptor implements Interceptor {

    public static final int CODE_OUTDATED_VERSION = 412;
    private final VersionUpdater versionUpdater;

    @Inject public VersionOutdatedErrorInterceptor(VersionUpdater versionUpdater) {
        this.versionUpdater = versionUpdater;
    }

    @Override public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.code() == CODE_OUTDATED_VERSION) {
            versionUpdater.notifyUpdateRequired();
        }
        return response;
    }
}
