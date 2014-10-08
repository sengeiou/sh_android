package gm.mobi.android.gcm;

import android.app.IntentService;
import android.content.Intent;
import gm.mobi.android.constant.GCMConstants;

public class GcmIntentService extends IntentService {
    public GcmIntentService() {
        super(GCMConstants.GCM_SENDER_ID);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
