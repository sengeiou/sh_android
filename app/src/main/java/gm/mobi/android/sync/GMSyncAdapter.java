package gm.mobi.android.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

public class GMSyncAdapter extends AbstractThreadedSyncAdapter {

    /**
     * Compatibility with versions previous to 3.0
     */
    public GMSyncAdapter(Context mContext, boolean autoInitialize){
        super(mContext,autoInitialize);
        setup();
    }

    /**
     * Android > 3.0
     */
    public GMSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        setup();
    }

    /**
     * Any initial setup tasks
     */
    public void setup(){
        /* no-op */
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        // Big boys stuff

    }
}
