package com.shootr.android.task;

import android.app.Application;
import com.path.android.jobqueue.network.NetworkUtil;
import javax.inject.Inject;

public class NetworkConnectionImpl implements NetworkConnection {

    private final Application application;
    private final NetworkUtil networkUtil;

    @Inject public NetworkConnectionImpl(Application application, NetworkUtil networkUtil) {
        this.application = application;
        this.networkUtil = networkUtil;
    }

    @Override public boolean isConnected() {
        return networkUtil.isConnected(application);
    }
}
