package gm.mobi.android.gcm;

import android.app.IntentService;
import android.content.Intent;

import gm.mobi.android.util.GCMUtils;

/**
 * Created by InmaculadaAlcon on 21/08/2014.
 */
public class GcmIntentService extends IntentService {
    public GcmIntentService() {
        super(GCMUtils.GCM_SENDER_ID);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
