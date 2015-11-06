package com.shootr.mobile.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.shootr.mobile.ShootrApplication;
import com.shootr.mobile.data.repository.sync.SyncTrigger;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Command in terminal: adb shell am broadcast -a com.shootr.android.ACTION_SYNC
 */
public class SyncBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_SYNC = "com.shootr.mobile.ACTION_SYNC";

    @Inject SyncTrigger syncTrigger;

    @Override public void onReceive(Context context, Intent intent) {
        Timber.d("-------> Sync triggered from intent");
        ShootrApplication.get(context).inject(this);
        new Thread(new Runnable() {
            @Override public void run() {
                syncTrigger.triggerSync();
            }
        }).start();
    }
}
