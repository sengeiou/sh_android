package gm.mobi.android.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by InmaculadaAlcon on 21/08/2014.
 */


public class GMBroadcastReceiver extends WakefulBroadcastReceiver {
    public static final String TAG = "gm.mobi.android.gcm.GMBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Entra en onReceive");
        ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
        startWakefulService(context,intent.setComponent(comp));
        setResultCode(Activity.RESULT_OK);
    }

}
