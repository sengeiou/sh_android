package com.shootr.android.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import com.shootr.android.constant.SyncConstants;
import javax.inject.Inject;
import timber.log.Timber;

import static com.shootr.android.constant.SyncConstants.AUTHORITY;

public class SyncConfigurator {

    private static final String CONTENT_AUTHORITY = SyncConstants.AUTHORITY;

    private final AccountManager accountManager;
    private Account dummyAccount;

    @Inject public SyncConfigurator(Application app) {
        this.dummyAccount = ShootrAccountService.getAccount();
        accountManager = (AccountManager) app.getSystemService(Context.ACCOUNT_SERVICE);
    }

    public void setupDefaultSyncing() {
        if (createSyncAccount()) {
            // Inform the system that this dummyAccount supports sync
            ContentResolver.setIsSyncable(dummyAccount, CONTENT_AUTHORITY, 1);
            // Inform the system that this dummyAccount is eligible for auto sync when the network is up
            setSyncAutomatically(true);
            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
           addPeriodicSyncs();
        }
    }

    public boolean createSyncAccount() {
        Timber.d("Creating account for sync");
        if (accountManager.addAccountExplicitly(dummyAccount, null, null)) {
            return true;
        } else {
            Timber.d("Account already exists");
            return false;
        }
    }

    public void setSyncAutomatically(boolean syncAutomatically) {
        ContentResolver.setSyncAutomatically(dummyAccount, CONTENT_AUTHORITY, syncAutomatically);
        Timber.d("Setting automatical syncing %s", syncAutomatically ? "on" : "off");
    }

    private void addPeriodicSyncs() {
        // Recommend a schedule for automatic synchronization. The system may modify this based
        // on other scheduled syncs and network utilization.
        addSyncInfoCleaner();
    }

    private void addSyncInfoCleaner(){
        Bundle syncParameters = new Bundle();
        ContentResolver.addPeriodicSync(dummyAccount, CONTENT_AUTHORITY, syncParameters,
          SyncConstants.SYNC_INTERVAL_FOR_INFO_CLEANER);
    }

    private void addSyncShots() {
        Timber.d("Adding periodic sync of shots");
        Bundle syncParameters = new Bundle();
        syncParameters.putInt(SyncConstants.CALL_TYPE, SyncConstants.GET_NEW_SHOTS_CALLTYPE);
        ContentResolver.addPeriodicSync(dummyAccount, CONTENT_AUTHORITY, syncParameters,
            SyncConstants.SYNC_INTERVAL_FOR_NEW_SHOTS);
    }

    private void addSyncFollowings() {
        Timber.d("Adding periodic sync of followings");
        Bundle paramsFollowing = new Bundle();
        paramsFollowing.putInt(SyncConstants.CALL_TYPE, SyncConstants.GET_FOLLOWINGS_CALLTYPE);
        ContentResolver.addPeriodicSync(dummyAccount, AUTHORITY, paramsFollowing,
            SyncConstants.SYNC_INTERVAL_FOLLOWINGS);
    }
}
