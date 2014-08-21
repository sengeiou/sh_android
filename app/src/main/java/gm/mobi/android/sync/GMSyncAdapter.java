package gm.mobi.android.sync;

import android.content.AbstractThreadedSyncAdapter;
import android.content.Context;

import gm.mobi.android.sync.connections.GMAsyncResponse;

/**
 * Created by InmaculadaAlcon on 21/08/2014.
 */
public class GMSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG_OFFSET = "offset";

    private Context mContext;
    private GMAsyncResponse mDelegate;


}
