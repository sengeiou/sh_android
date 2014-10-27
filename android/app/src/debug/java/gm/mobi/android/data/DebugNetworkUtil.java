package gm.mobi.android.data;

import android.app.Application;
import android.content.Context;
import com.path.android.jobqueue.network.NetworkUtilImpl;
import gm.mobi.android.data.prefs.BooleanPreference;
import gm.mobi.android.data.prefs.StringPreference;
import javax.inject.Inject;


public class DebugNetworkUtil extends NetworkUtilImpl {

    private StringPreference selectedEndpoint;
    private BooleanPreference isNetworkEnabled;

    @Inject public DebugNetworkUtil(Application context, @ApiEndpoint StringPreference selectedEndpoint, @NetworkEnabled BooleanPreference isNetworkEnabled) {
        super(context);
        this.selectedEndpoint = selectedEndpoint;
        this.isNetworkEnabled = isNetworkEnabled;
    }

    @Override
    public boolean isConnected(Context context) {
        return !(ApiEndpoints.isMockMode(selectedEndpoint.get()) && !isNetworkEnabled.get()) && super.isConnected(context);
    }
}
