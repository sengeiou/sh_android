package com.shootr.android.data;

import android.app.Application;
import android.content.Context;
import com.path.android.jobqueue.network.NetworkUtilImpl;
import com.shootr.android.data.prefs.BooleanPreference;
import javax.inject.Inject;


public class DebugNetworkUtil extends NetworkUtilImpl {

    private BooleanPreference isNetworkEnabled;

    @Inject public DebugNetworkUtil(Application context, @NetworkEnabled BooleanPreference isNetworkEnabled) {
        super(context);
        this.isNetworkEnabled = isNetworkEnabled;
    }

    @Override
    public boolean isConnected(Context context) {
        return isNetworkEnabled.get() && super.isConnected(context);
    }
}
