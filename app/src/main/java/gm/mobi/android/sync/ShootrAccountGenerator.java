package gm.mobi.android.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import gm.mobi.android.constant.Constants;
import gm.mobi.android.constant.SyncConstants;

import static gm.mobi.android.constant.SyncConstants.AUTHORITY;

public class ShootrAccountGenerator {

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    //TODO crear una cuenta que tenga al menos relación con el usuario real, por aprovechar el framework
    public static Account createSyncAccount(Context context) {
        //Create account type and default account
        //Get an instance of Android Account Manager
        //Add the account name and type, no password or user data. If ok, return the Account object, otherwise report an error
        Account mAccount = new Account(SyncConstants.ACCOUNT_NAME, SyncConstants.ACCOUNT_TYPE);
        AccountManager mAccountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        if (mAccountManager.addAccountExplicitly(mAccount, null, null)) {
             /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            ContentResolver.setIsSyncable(mAccount,SyncConstants.AUTHORITY,1);
            ContentResolver.setSyncAutomatically(mAccount,SyncConstants.AUTHORITY,true);

            Bundle params  = new Bundle();
            params.putInt(SyncConstants.CALL_TYPE,SyncConstants.REMOVE_OLD_SHOTS);
            ContentResolver.addPeriodicSync(mAccount, AUTHORITY, params,SyncConstants.SYNC_INTERVAL_SHOTS);

        } else {
            /*
             * TODO The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */

        }
        return mAccount;
    }
}
