package gm.mobi.android.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import gm.mobi.android.constant.Constants;
import gm.mobi.android.constant.SyncConstants;

public class GMAccountGenerator {

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
        Account newAccount = new Account(SyncConstants.ACCOUNT_NAME, SyncConstants.ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
             /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * TODO The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }
}
