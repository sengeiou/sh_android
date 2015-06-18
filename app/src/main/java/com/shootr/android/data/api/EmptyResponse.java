package com.shootr.android.data.api;

import retrofit.ResponseCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class EmptyResponse extends ResponseCallback {

    @Override
    public void success(Response response) {
        /* no-op */
    }

    @Override
    public void failure(RetrofitError error) {
        Timber.e(error, "Empry response error!: %s", error.getMessage());
    }
}
