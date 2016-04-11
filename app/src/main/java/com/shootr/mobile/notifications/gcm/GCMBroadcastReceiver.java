package com.shootr.mobile.notifications.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import timber.log.Timber;

public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("Received GCM broadcast intent. Launching IntentService");
        ComponentName comp = new ComponentName(context.getPackageName(), GCMIntentService.class.getName());
        startWakefulService(context,intent.setComponent(comp));
        setResultCode(Activity.RESULT_OK);
    }

}
