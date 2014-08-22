package gm.mobi.android.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import gm.mobi.android.sync.connections.GMAsyncResponse;

/**
 * Created by InmaculadaAlcon on 21/08/2014.
 */
public class GMSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG_OFFSET = "offset";

    private Context mContext;
    private GMAsyncResponse mDelegate;
    public GMSyncAdapter(Context mContext, boolean autoInitialize){
        super(mContext,autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

    }
}
