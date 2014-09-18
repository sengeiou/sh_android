package gm.mobi.android.data;

import android.app.Application;
import android.content.Context;

import com.path.android.jobqueue.network.NetworkUtilImpl;

import javax.inject.Inject;

import gm.mobi.android.data.prefs.BooleanPreference;
import gm.mobi.android.data.prefs.StringPreference;
import hugo.weaving.DebugLog;


public class DebugNetworkUtil extends NetworkUtilImpl {

    private StringPreference selectedEndpoint;
    private BooleanPreference isNetworkEnabled;

    @Inject public DebugNetworkUtil(Application context, @ApiEndpoint StringPreference selectedEndpoint, @NetworkEnabled BooleanPreference isNetworkEnabled) {
        super(context);
        this.selectedEndpoint = selectedEndpoint;
        this.isNetworkEnabled = isNetworkEnabled;
    }

    @Override
    @DebugLog
    public boolean isConnected(Context context) {
        return !(ApiEndpoints.isMockMode(selectedEndpoint.get()) && !isNetworkEnabled.get()) && super.isConnected(context);
    }
}
